package com.patternverifier.archunitcomparison;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Confronto con pattern-verifier — Pattern: Template Method
 *
 * pattern-verifier (2 righe):
 *   PatternAssertions.assertThat(DataProcessor.class)
 *       .implementsTemplateMethod().withTemplateMethod("process");
 *
 * ArchUnit (questo file): 3 regole implementate + 1 non implementabile.
 * - Check 1: built-in (beAbstract)
 * - Check 2-3: ArchCondition custom
 * - Check 4: @Disabled — limite fondamentale di ArchUnit per l'analisi del corpo dei metodi
 *
 * Limite fondamentale: la verifica chiave del Template Method ("il metodo template chiama
 * i passi astratti della stessa classe") richiede l'ispezione del corpo di un metodo
 * specifico. ArchUnit non offre primitive per questo a livello di singolo metodo —
 * scendere a quel livello richiede la stessa logica ASM del nostro TemplateMethodBodyAnalyzer.
 */
class ArchUnitTemplateMethodTest {

    private static final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.patternverifier.templatemethod.correct");

    // Check 1 — classe astratta: built-in disponibile tramite haveModifier
    @Test
    void templateMethod_check1_classMustBeAbstract() {
        classes()
                .that().haveSimpleName("DataProcessor")
                .should().haveModifier(JavaModifier.ABSTRACT)
                .check(importedClasses);
    }

    // Check 2 — ha metodo concreto (template) con il nome dato: nessuna built-in
    @Test
    void templateMethod_check2_mustHaveConcreteTemplateMethod() {
        ArchCondition<JavaClass> haveConcreteMethodNamedProcess =
                new ArchCondition<>("have a non-abstract method named 'process'") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getMethods().stream()
                                .anyMatch(m -> m.getName().equals("process")
                                        && !m.getModifiers().contains(JavaModifier.ABSTRACT));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " has no non-abstract method named 'process'"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("DataProcessor")
                .should(haveConcreteMethodNamedProcess)
                .check(importedClasses);
    }

    // Check 3 — ha almeno un metodo astratto (i passi): nessuna built-in
    @Test
    void templateMethod_check3_mustHaveAbstractStepMethods() {
        ArchCondition<JavaClass> haveAbstractStepMethods =
                new ArchCondition<>("have at least one abstract method (template step)") {
                    @Override
                    public void check(JavaClass clazz, ConditionEvents events) {
                        boolean found = clazz.getMethods().stream()
                                .anyMatch(m -> m.getModifiers().contains(JavaModifier.ABSTRACT));
                        if (!found) {
                            events.add(SimpleConditionEvent.violated(clazz,
                                    clazz.getSimpleName() + " has no abstract methods (template steps)"));
                        }
                    }
                };

        classes()
                .that().haveSimpleName("DataProcessor")
                .should(haveAbstractStepMethods)
                .check(importedClasses);
    }

    // Check 4 — il metodo template chiama i passi astratti della stessa classe: NON IMPLEMENTABILE
    //
    // In pattern-verifier: TemplateMethodBodyAnalyzer scansiona le istruzioni bytecode di
    // "process()" cercando INVOKEVIRTUAL verso metodi abstract della stessa classe. È un
    // MethodVisitor ASM che opera sul corpo di uno specifico metodo.
    //
    // In ArchUnit: getMethodCallsFromSelf() opera a livello di classe intera, non di singolo
    // metodo. Per isolare le chiamate fatte da "process()" bisogna recuperare il JavaMethod
    // corrispondente e ispezionare le sue istruzioni — ma ArchUnit non espone il bytecode
    // delle istruzioni (INVOKEVIRTUAL, INVOKEINTERFACE ecc.) come fa ASM. La logica
    // necessaria sarebbe equivalente a scrivere il MethodVisitor direttamente, eliminando
    // qualsiasi vantaggio di astrazione fornito da ArchUnit.
    @Test
    @Disabled("ArchUnit non offre primitive per ispezionare il corpo di un singolo metodo " +
              "specifico. Verificare che 'process()' chiami metodi abstract della stessa " +
              "classe richiederebbe logica bytecode-level equivalente a un MethodVisitor ASM.")
    void templateMethod_check4_templateMethodMustCallAbstractSteps() {
        // Non implementato. Il check equivalente in pattern-verifier è in
        // TemplateMethodVerifier.checkTemplateMethodCallsAbstractSteps()
        // via TemplateMethodBodyAnalyzer.
    }
}
