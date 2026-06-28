package com.patternverifier.archunitcomparison;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Confronto con pattern-verifier — Pattern: Singleton
 *
 * pattern-verifier (1 riga):
 *   PatternAssertions.assertThat(DatabaseConnection.class).implementsSingleton();
 *
 * ArchUnit (questo file): 3 regole separate, 2 delle quali richiedono ArchCondition custom
 * perché ArchUnit non ha primitive per "campo static del proprio tipo" né
 * "metodo static che ritorna il proprio tipo".
 */
class ArchUnitSingletonTest {

    private static final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.patternverifier.singleton.correct");

    // Check 1 — costruttori private: esiste una built-in, 1 riga
    @Test
    void singleton_check1_allConstructorsMustBePrivate() {
        classes()
                .that().haveSimpleName("DatabaseConnection")
                .should().haveOnlyPrivateConstructors()
                .check(importedClasses);
    }

    // Check 2 — campo static del proprio tipo: nessuna built-in, ArchCondition custom necessaria
    @Test
    void singleton_check2_mustHaveStaticFieldOfOwnType() {
        ArchCondition<JavaClass> haveStaticFieldOfOwnType =
                new ArchCondition<>("have a static field of its own type") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getFields().stream()
                                .anyMatch(f -> f.getModifiers().contains(JavaModifier.STATIC)
                                        && f.getRawType().getName().equals(clazz.getName()));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " does not have a static field of its own type"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("DatabaseConnection")
                .should(haveStaticFieldOfOwnType)
                .check(importedClasses);
    }

    // Check 3 — metodo static getter: nessuna built-in, ArchCondition custom necessaria
    @Test
    void singleton_check3_mustHaveStaticGetterReturningOwnType() {
        ArchCondition<JavaClass> haveStaticGetterReturningOwnType =
                new ArchCondition<>("have a static method returning its own type") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getMethods().stream()
                                .anyMatch(m -> m.getModifiers().contains(JavaModifier.STATIC)
                                        && m.getRawReturnType().getName().equals(clazz.getName()));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " does not have a static method returning its own type"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("DatabaseConnection")
                .should(haveStaticGetterReturningOwnType)
                .check(importedClasses);
    }
}
