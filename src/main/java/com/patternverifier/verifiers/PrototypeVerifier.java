package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInfo;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

/**
 * Verifica il pattern Prototype: un oggetto viene creato clonando un'istanza esemplare invece di
 * istanziare direttamente una classe concreta.
 *
 * <p>Il ruolo interessante non è il solo {@code ConcretePrototype} (che si riduce a "esiste un
 * metodo di clonazione"), ma il {@code Client}: è lui a detenere il prototipo come campo e a
 * invocarne la clonazione invece di usare {@code new}. Quest'ultimo è un controllo di interazione
 * (Livello 2), della stessa forma della delega verificata in Adapter, Decorator o Strategy.
 */
public class PrototypeVerifier {

    private static final String[] CLONE_METHOD_PREFIXES = {"clone", "copy", "duplicate"};

    private final ClassMetadata concretePrototype;
    private final ClassMetadata prototype;
    /** Opzionale: null se il Client non è stato dichiarato. */
    private final ClassMetadata client;

    public PrototypeVerifier(ClassMetadata concretePrototype, ClassMetadata prototype) {
        this(concretePrototype, prototype, null);
    }

    public PrototypeVerifier(ClassMetadata concretePrototype, ClassMetadata prototype,
                             ClassMetadata client) {
        this.concretePrototype = concretePrototype;
        this.prototype = prototype;
        this.client = client;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkPrototypeIsAbstract(violations);
        checkPrototypeDeclaresCloneMethod(violations);
        checkConcretePrototypeConformsToPrototype(violations);
        checkConcretePrototypeHasConcreteCloneMethod(violations);
        if (client != null) {
            checkClientHasPrototypeField(violations);
            checkClientInvokesPrototype(violations);
        }
        return violations;
    }

    private void checkPrototypeIsAbstract(List<String> violations) {
        if (!prototype.isInterface() && !prototype.isAbstract()) {
            violations.add(prototype.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — il Prototype deve definire un contratto astratto di clonazione,"
                    + " così che il Client possa clonare senza conoscere la classe concreta");
        }
    }

    // Naming convention per l'operazione di clonazione: clone*, copy*, duplicate*.
    // Non si verifica il tipo di ritorno: java.lang.Object.clone() restituisce Object, e imporre
    // il tipo del Prototype escluderebbe l'idioma Java standard (covarianza introdotta solo in
    // Java 5, assente in gran parte del codice reale).
    private void checkPrototypeDeclaresCloneMethod(List<String> violations) {
        if (!hasCloneMethod(prototype, false)) {
            violations.add(prototype.getSimpleName()
                    + " non dichiara un metodo di clonazione (clone*, copy*, duplicate*)"
                    + " — il Prototype deve esporre l'operazione con cui il Client crea le copie");
        }
    }

    private void checkConcretePrototypeConformsToPrototype(List<String> violations) {
        if (!TypeHierarchy.isAssignable(concretePrototype.getClassName(), prototype.getClassName())) {
            violations.add(concretePrototype.getSimpleName()
                    + " non implementa né estende "
                    + prototype.getSimpleName()
                    + " — il ConcretePrototype deve conformarsi al tipo Prototype");
        }
    }

    private void checkConcretePrototypeHasConcreteCloneMethod(List<String> violations) {
        if (!hasCloneMethod(concretePrototype, true)) {
            violations.add(concretePrototype.getSimpleName()
                    + " non ha un'implementazione concreta di un metodo di clonazione"
                    + " (clone*, copy*, duplicate*)"
                    + " — il ConcretePrototype deve saper produrre una copia di sé stesso");
        }
    }

    private void checkClientHasPrototypeField(List<String> violations) {
        if (client.isInterface()) {
            violations.add(client.getSimpleName()
                    + " è un'interfaccia — non può dichiarare un campo istanza di tipo "
                    + prototype.getSimpleName()
                    + ": il controllo sul campo non è applicabile a un'interfaccia,"
                    + " verificare le classi concrete che la implementano");
            return;
        }
        String prototypeName = prototype.getClassName();
        boolean hasField = client.getFields().stream()
                .anyMatch(f -> TypeHierarchy.isAssignable(f.getTypeName(), prototypeName));
        if (!hasField) {
            violations.add(client.getSimpleName()
                    + " non ha un campo di tipo "
                    + prototype.getSimpleName()
                    + " — il Client deve conservare l'istanza prototipale da clonare");
        }
    }

    // Livello 2: il Client deve realmente invocare il prototipo, non limitarsi a detenerlo.
    // È la proprietà che distingue un Prototype da una classe che tiene un esemplare inutilizzato.
    private void checkClientInvokesPrototype(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(client.getClassName(), prototype.getClassName())) {
            violations.add(client.getSimpleName()
                    + " non invoca mai metodi su "
                    + prototype.getSimpleName()
                    + " — il Client deve creare le nuove istanze clonando il prototipo,"
                    + " non istanziando direttamente una classe concreta");
        }
    }

    private boolean hasCloneMethod(ClassMetadata type, boolean requireConcrete) {
        return type.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .filter(m -> !requireConcrete || !m.isAbstract())
                .anyMatch(this::isCloneMethodName);
    }

    private boolean isCloneMethodName(MethodInfo m) {
        for (String prefix : CLONE_METHOD_PREFIXES) {
            if (m.getName().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
