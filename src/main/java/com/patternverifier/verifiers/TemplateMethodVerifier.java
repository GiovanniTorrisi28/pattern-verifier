package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInfo;
import com.patternverifier.core.TemplateMethodBodyAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TemplateMethodVerifier {

    private final Class<?> clazz;
    private final ClassMetadata abstractClass;
    private final String templateMethodName;

    public TemplateMethodVerifier(Class<?> clazz, ClassMetadata abstractClass,
                                   String templateMethodName) {
        this.clazz = clazz;
        this.abstractClass = abstractClass;
        this.templateMethodName = templateMethodName;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkIsAbstractClass(violations);
        checkHasConcreteMethod(violations);
        checkHasAbstractSteps(violations);
        checkTemplateMethodCallsAbstractSteps(violations);
        return violations;
    }

    private void checkIsAbstractClass(List<String> violations) {
        if (!abstractClass.isAbstract() || abstractClass.isInterface()) {
            violations.add(abstractClass.getSimpleName()
                    + " deve essere una classe astratta (non un'interfaccia)"
                    + " — il Template Method canonico richiede una classe astratta"
                    + " che definisca lo scheletro dell'algoritmo");
        }
    }

    private void checkHasConcreteMethod(List<String> violations) {
        boolean found = abstractClass.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> !m.isAbstract());
        if (!found) {
            violations.add(abstractClass.getSimpleName()
                    + " non ha metodi concreti — il Template Method deve avere almeno"
                    + " un metodo non-astratto che definisce lo scheletro dell'algoritmo");
        }
    }

    private void checkHasAbstractSteps(List<String> violations) {
        boolean found = abstractClass.getMethods().stream()
                .anyMatch(MethodInfo::isAbstract);
        if (!found) {
            violations.add(abstractClass.getSimpleName()
                    + " non ha metodi astratti — il Template Method deve delegare almeno"
                    + " un passo alle sottoclassi tramite metodi astratti");
        }
    }

    // Usa MethodVisitor (analisi del corpo) per verificare che il template method
    // invochi concretamente i passi astratti via INVOKEVIRTUAL sulla stessa classe.
    // Non è l'unica verifica che legge istruzioni bytecode — MethodInvocationAnalyzer lo fa per
    // altri 11 verifier — ma è l'unica mirata su un singolo metodo indicato dal programmatore,
    // invece che sull'intera classe.
    private void checkTemplateMethodCallsAbstractSteps(List<String> violations) {
        Set<String> abstractStepNames = abstractClass.getMethods().stream()
                .filter(MethodInfo::isAbstract)
                .map(MethodInfo::getName)
                .collect(Collectors.toSet());

        Set<String> calledMethods =
                TemplateMethodBodyAnalyzer.findCalledMethods(clazz, templateMethodName);

        boolean callsAbstractStep = calledMethods.stream()
                .anyMatch(abstractStepNames::contains);

        if (!callsAbstractStep) {
            violations.add("Il metodo '" + templateMethodName + "' non invoca metodi astratti"
                    + " della classe " + abstractClass.getSimpleName()
                    + " — il template method deve richiamare i passi astratti nel proprio corpo");
        }
    }
}
