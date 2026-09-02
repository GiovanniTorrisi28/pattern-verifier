package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.CollectionTypes;
import com.patternverifier.core.FieldInfo;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

/**
 * Verifica il pattern Mediator: un oggetto incapsula il modo in cui un insieme di oggetti
 * interagisce, così che i Colleague non debbano riferirsi esplicitamente l'uno all'altro.
 *
 * <p>È l'unico verificatore del tool fondato su una <b>proprietà negativa</b>: non che qualcosa
 * sia presente, ma che qualcosa sia <i>assente</i> — nessun Colleague deve detenere un
 * riferimento diretto a un altro Colleague. Verificare un'assenza è in generale intrattabile per
 * un rilevatore automatico, che dovrebbe esaminare l'intero classpath per sapere quali classi
 * siano Colleague. Nel modello dichiarativo adottato qui l'insieme è invece <b>finito e noto</b>,
 * perché è il programmatore a enumerarlo: l'assenza diventa così verificabile su un dominio
 * chiuso. È l'illustrazione più diretta del vantaggio del conformance checking sulla detection.
 *
 * <p><b>Confine deliberato</b>: si verifica l'assenza di un <i>campo</i> di tipo Colleague, non
 * l'assenza di <i>invocazioni</i> verso altri Colleague. GoF chiede che gli oggetti non si
 * riferiscano esplicitamente l'uno all'altro, e un riferimento esplicito è un campo. Un Colleague
 * che ottenga un altro Colleague <i>attraverso</i> il Mediator e ne invochi un metodo sta usando
 * il pattern correttamente: segnalarlo produrrebbe falsi positivi. Il controllo si ferma quindi al
 * Livello 1, per scelta e non per limite tecnico.
 */
public class MediatorVerifier {

    private final ClassMetadata concreteMediator;
    private final ClassMetadata mediator;
    private final List<ClassMetadata> colleagues;

    public MediatorVerifier(ClassMetadata concreteMediator, ClassMetadata mediator,
                            List<ClassMetadata> colleagues) {
        this.concreteMediator = concreteMediator;
        this.mediator = mediator;
        this.colleagues = colleagues;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkDeclaresEnoughColleagues(violations);
        checkMediatorIsAbstract(violations);
        checkConcreteMediatorConformsToMediator(violations);
        checkConcreteMediatorKnowsColleagues(violations);
        checkColleaguesKnowMediator(violations);
        checkColleaguesDoNotReferenceEachOther(violations);
        return violations;
    }

    // Con un solo Colleague non esiste alcuna interazione da mediare: la proprietà di
    // disaccoppiamento è vacuamente vera e la verifica perderebbe contenuto informativo.
    private void checkDeclaresEnoughColleagues(List<String> violations) {
        if (colleagues.size() < 2) {
            violations.add(concreteMediator.getSimpleName()
                    + " dichiara " + colleagues.size() + " Colleague"
                    + " — il Mediator deve coordinare almeno 2 Colleague,"
                    + " altrimenti non c'è alcuna interazione da mediare");
        }
    }

    private void checkMediatorIsAbstract(List<String> violations) {
        if (!mediator.isInterface() && !mediator.isAbstract()) {
            violations.add(mediator.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — il Mediator deve definire un contratto astratto di coordinamento,"
                    + " così che i Colleague dipendano da esso e non da una classe concreta");
        }
    }

    private void checkConcreteMediatorConformsToMediator(List<String> violations) {
        if (!TypeHierarchy.isAssignable(concreteMediator.getClassName(), mediator.getClassName())) {
            violations.add(concreteMediator.getSimpleName()
                    + " non implementa né estende "
                    + mediator.getSimpleName()
                    + " — il ConcreteMediator deve realizzare il contratto del Mediator");
        }
    }

    // GoF, sezione Partecipanti: "ConcreteMediator knows and maintains its colleagues". Il verbo
    // chiave è *maintains*: mantenere significa conservare come stato, cioè in un campo. Non basta
    // quindi un metodo che accetti un Colleague (sarebbe indistinguibile dal metodo di mediazione,
    // che pure riceve un partecipante come mittente) — serve che il coordinatore ne detenga almeno
    // uno. È la spia strutturale che distingue un vero ConcreteMediator da un'interfaccia
    // implementata "a vuoto". Due forme accettate:
    //   (a) un campo del tipo di un Colleague (confronto bidirezionale su supertipo/sottotipo) —
    //       es. DrawApplication con "private Tool fTool" e "private StandardDrawingView fView";
    //   (b) un campo Collection che può contenere un Colleague — es. la fixture ChatRoom con
    //       "List<Participant>".
    // Non basta un solo Colleague mantenuto su due? Sì: il coordinatore mantiene i partecipanti,
    // ma non è tenuto a detenerli tutti come campi separati (può ottenerne alcuni di riflesso da
    // altri). Richiedere che ne mantenga almeno uno coglie l'intento senza generare falsi positivi.
    private void checkConcreteMediatorKnowsColleagues(List<String> violations) {
        if (concreteMediator.isInterface()) {
            return; // un'interfaccia non può mantenere stato: il vincolo non le si applica
        }
        boolean knows = colleagues.stream().anyMatch(this::isMaintainedBy);
        if (!knows) {
            violations.add(concreteMediator.getSimpleName()
                    + " non mantiene alcun riferimento ai propri Colleague"
                    + " (nessun campo di tipo Colleague, nessuna Collection di Colleague)"
                    + " — il ConcreteMediator deve conoscere e mantenere i partecipanti che coordina");
        }
    }

    private boolean isMaintainedBy(ClassMetadata colleague) {
        String colleagueType = colleague.getClassName();
        boolean fieldOfColleagueType = concreteMediator.getFields().stream()
                .anyMatch(f -> isReferenceTo(f.getTypeName(), colleagueType));
        boolean collectionOfColleagues = concreteMediator.getFields().stream()
                .anyMatch(f -> maintainsColleagueInCollection(f, colleagueType));
        return fieldOfColleagueType || collectionOfColleagues;
    }

    /**
     * Vero se il campo è una Collection che può contenere il Colleague. A differenza di
     * {@link CollectionTypes#isCollectionOf} — pensato per Composite/Observer, dove il ruolo
     * dichiarato è il supertipo comune e la collezione ne contiene i sottotipi — qui la relazione
     * è tipicamente invertita: la collezione del Mediator è tipizzata sul supertipo dei Colleague
     * (es. {@code List<Participant>}) mentre i Colleague dichiarati sono i sottotipi concreti
     * ({@code UserColleague}). Si accetta quindi l'assegnabilità in entrambe le direzioni; su una
     * Collection raw (nessuna {@code Signature}) si resta permissivi, come altrove nel tool.
     */
    private boolean maintainsColleagueInCollection(FieldInfo field, String colleagueType) {
        if (!CollectionTypes.KNOWN.contains(field.getTypeName())) {
            return false;
        }
        String elementType = field.getGenericElementTypeName();
        if (elementType == null) {
            return true; // Collection raw: non possiamo negare la corrispondenza
        }
        return TypeHierarchy.isAssignable(colleagueType, elementType)
                || TypeHierarchy.isAssignable(elementType, colleagueType);
    }

    private void checkColleaguesKnowMediator(List<String> violations) {
        String mediatorName = mediator.getClassName();
        for (ClassMetadata colleague : colleagues) {
            if (colleague.isInterface()) {
                violations.add(colleague.getSimpleName()
                        + " è un'interfaccia — non può dichiarare un campo istanza di tipo "
                        + mediator.getSimpleName()
                        + ": il controllo sul campo non è applicabile a un'interfaccia,"
                        + " verificare le classi concrete che la implementano");
                continue;
            }
            boolean knowsMediator = colleague.getFields().stream()
                    .anyMatch(f -> TypeHierarchy.isAssignable(f.getTypeName(), mediatorName));
            if (!knowsMediator) {
                violations.add(colleague.getSimpleName()
                        + " non ha un campo di tipo "
                        + mediator.getSimpleName()
                        + " — ogni Colleague deve comunicare attraverso il Mediator,"
                        + " e per farlo deve mantenerne un riferimento");
            }
        }
    }

    // La proprietà distintiva del pattern, espressa in negativo.
    private void checkColleaguesDoNotReferenceEachOther(List<String> violations) {
        for (ClassMetadata colleague : colleagues) {
            if (colleague.isInterface()) {
                continue; // un'interfaccia non ha campi istanza: nulla da verificare
            }
            for (ClassMetadata other : colleagues) {
                if (other.getClassName().equals(colleague.getClassName())) {
                    continue;
                }
                boolean referencesOther = colleague.getFields().stream()
                        .anyMatch(f -> isReferenceTo(f.getTypeName(), other.getClassName()));
                if (referencesOther) {
                    violations.add(colleague.getSimpleName()
                            + " ha un campo di tipo "
                            + other.getSimpleName()
                            + ", che è un altro Colleague"
                            + " — nel Mediator i Colleague non devono riferirsi direttamente"
                            + " l'uno all'altro, ma solo attraverso il Mediator");
                }
            }
        }
    }

    /**
     * Stabilisce se un campo di tipo {@code fieldType} costituisce un riferimento al Colleague
     * {@code colleagueType}. Il confronto è <b>bidirezionale</b>: conta sia il campo tipizzato
     * con il Colleague o un suo sottotipo, sia — caso più frequente nel codice reale — il campo
     * tipizzato con un supertipo che può contenerlo. Su JHotDraw è quest'ultima la forma
     * ricorrente: {@code AbstractTool} dichiara {@code DrawingView fView}, e
     * {@code StandardDrawingView} è un Colleague dichiarato: il riferimento diretto esiste, anche
     * se mediato dall'interfaccia. Un confronto unidirezionale lo lascerebbe sfuggire.
     *
     * <p>I tipi di libreria sono esclusi dalla direzione "supertipo": senza questa esclusione un
     * campo di tipo {@code Object} o {@code Serializable} risulterebbe un riferimento a qualunque
     * Colleague, generando falsi positivi sistematici.
     */
    private boolean isReferenceTo(String fieldType, String colleagueType) {
        if (TypeHierarchy.isAssignable(fieldType, colleagueType)) {
            return true;
        }
        if (isLibraryType(fieldType)) {
            return false;
        }
        return TypeHierarchy.isAssignable(colleagueType, fieldType);
    }

    private boolean isLibraryType(String typeName) {
        return typeName.startsWith("java.") || typeName.startsWith("javax.");
    }
}
