package com.patternverifier.archunitcomparison;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Confronto con pattern-verifier — Pattern: Observer
 *
 * pattern-verifier (2 righe):
 *   PatternAssertions.assertThat(EventBus.class)
 *       .implementsObserver().withObserver(EventListener.class);
 *
 * ArchUnit (questo file): 6 regole separate.
 * - Check 1: built-in (beInterfaces)
 * - Check 2-5: ArchCondition custom — ArchUnit non ha primitive per naming convention (startsWith)
 * - Check 6: parzialmente fattibile via getMethodCallsFromSelf(), ma opera a livello di classe
 *   (non distingue "invocazione tramite il campo Observer" da qualsiasi chiamata a EventListener)
 *
 * Limite fondamentale di granularità: ArchUnit verifica "tutte le classi nel package che
 * soddisfano il predicato", non "questa specifica istanza del pattern". Le 6 regole non sanno
 * di essere collegate dallo stesso Observer — un'unica violazione genera 6 test failure separati.
 */
class ArchUnitObserverTest {

    private static final Set<String> COLLECTION_TYPES = Set.of(
            "java.util.List", "java.util.ArrayList", "java.util.LinkedList",
            "java.util.Set", "java.util.HashSet", "java.util.LinkedHashSet",
            "java.util.Collection", "java.util.Queue", "java.util.Deque", "java.util.ArrayDeque"
    );

    private static final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.patternverifier.observer.correct");

    // Check 1 — Observer è un'interfaccia: built-in disponibile
    @Test
    void observer_check1_observerMustBeInterface() {
        classes()
                .that().haveSimpleName("EventListener")
                .should().beInterfaces()
                .check(importedClasses);
    }

    // Check 2 — Observer ha metodo di callback: nessuna built-in per prefix matching
    @Test
    void observer_check2_observerMustHaveCallbackMethod() {
        ArchCondition<JavaClass> haveCallbackMethod =
                new ArchCondition<>("have a method named update*, on*, or handle*") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getMethods().stream()
                                .anyMatch(m -> m.getName().startsWith("update")
                                        || m.getName().startsWith("on")
                                        || m.getName().startsWith("handle"));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " has no callback method named update*, on*, or handle*"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("EventListener")
                .should(haveCallbackMethod)
                .check(importedClasses);
    }

    // Check 3 — Subject ha campo Collection: nessuna built-in per "campo di tipo Collection"
    @Test
    void observer_check3_subjectMustHaveCollectionField() {
        ArchCondition<JavaClass> haveCollectionField =
                new ArchCondition<>("have a field of a Collection type") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getFields().stream()
                                .anyMatch(f -> COLLECTION_TYPES.contains(f.getRawType().getName()));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " has no Collection field for storing observers"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("EventBus")
                .should(haveCollectionField)
                .check(importedClasses);
    }

    // Check 4 — Subject ha metodo di registrazione: nessuna built-in per naming convention
    @Test
    void observer_check4_subjectMustHaveRegisterMethod() {
        ArchCondition<JavaClass> haveRegisterMethod =
                new ArchCondition<>("have a register method (add*, register*, or subscribe*) accepting an Observer") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getMethods().stream()
                                .anyMatch(m -> (m.getName().startsWith("add")
                                        || m.getName().startsWith("register")
                                        || m.getName().startsWith("subscribe"))
                                        && m.getRawParameterTypes().stream()
                                                .anyMatch(p -> p.getSimpleName().equals("EventListener")));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " has no register method (add*/register*/subscribe*) accepting EventListener"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("EventBus")
                .should(haveRegisterMethod)
                .check(importedClasses);
    }

    // Check 5 — Subject ha metodo di notifica: nessuna built-in per naming convention
    @Test
    void observer_check5_subjectMustHaveNotifyMethod() {
        ArchCondition<JavaClass> haveNotifyMethod =
                new ArchCondition<>("have a notify method (notify*, fire*, or dispatch*)") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getMethods().stream()
                                .anyMatch(m -> m.getName().startsWith("notify")
                                        || m.getName().startsWith("fire")
                                        || m.getName().startsWith("dispatch"));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " has no notify method (notify*/fire*/dispatch*)"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("EventBus")
                .should(haveNotifyMethod)
                .check(importedClasses);
    }

    // Check 6 — Subject invoca metodi sull'Observer: parzialmente fattibile
    // Nota: getMethodCallsFromSelf() aggrega le chiamate da tutti i metodi della classe,
    // inclusi i metodi sintetici generati dalle lambda. Verifica però a livello di classe
    // intera, non che l'invocazione avvenga tramite il campo Collection<Observer>.
    @Test
    void observer_check6_subjectMustInvokeObserverMethods() {
        ArchCondition<JavaClass> invokesObserverMethods =
                new ArchCondition<>("call at least one method on EventListener") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getMethodCallsFromSelf().stream()
                                .anyMatch(call -> call.getTarget().getOwner().getFullName()
                                        .equals("com.patternverifier.observer.correct.EventListener"));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " never calls any method on EventListener"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("EventBus")
                .should(invokesObserverMethods)
                .check(importedClasses);
    }
}
