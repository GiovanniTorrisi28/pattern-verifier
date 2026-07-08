package com.patternverifier.core;

import java.util.Set;

/**
 * Tipi di collezione riconosciuti dai verifier come campo valido per contenere una pluralità
 * di elementi (i figli di un Composite, gli observer di un Subject, ecc.).
 *
 * <p>Centralizzato qui perché più verifier ({@link com.patternverifier.verifiers.CompositeVerifier},
 * {@link com.patternverifier.verifiers.ObserverVerifier}) ne hanno bisogno e in passato ne
 * mantenevano copie separate che sono divergute silenziosamente (una includeva {@code Queue}/
 * {@code Deque}/{@code ArrayDeque}, l'altra no — un Composite con i figli in un {@code ArrayDeque}
 * falliva mentre un Observer con i listener in un {@code ArrayDeque} passava). Un'unica fonte
 * evita che l'aggiunta di un tipo in un verifier non si rifletta nell'altro.
 *
 * <p>{@code Vector} è incluso: implementa {@code List} dal JDK 1.2 ed è la collezione usata
 * convenzionalmente nel codice precedente al Collections Framework (es. JHotDraw, 1997).
 */
public final class CollectionTypes {

    private CollectionTypes() {}

    public static final Set<String> KNOWN = Set.of(
            "java.util.Collection",
            "java.util.List", "java.util.ArrayList", "java.util.LinkedList", "java.util.Vector",
            "java.util.Set", "java.util.HashSet", "java.util.LinkedHashSet", "java.util.TreeSet",
            "java.util.Queue", "java.util.Deque", "java.util.ArrayDeque"
    );

    /**
     * Vero se il campo è di un tipo Collection noto <b>e</b>, quando l'informazione è
     * disponibile, il suo argomento generico è assegnabile a {@code expectedElementTypeFqn}.
     *
     * <p>Il tipo generico di una Collection è cancellato a livello di istruzioni bytecode (type
     * erasure), ma il compilatore lo conserva in un attributo {@code Signature} separato, letto
     * qui via {@link FieldInfo#getGenericElementTypeName()}. Se il campo è dichiarato con un tipo
     * <b>raw</b> (senza generics — l'unica forma possibile in codice pre-Java 5, es. JHotDraw
     * 1997, e comunque legale in codice più recente), non esiste alcun attributo {@code
     * Signature} da leggere: in quel caso il controllo generico è saltato e il campo viene
     * accettato per il solo tipo raw — non possiamo né confermare né negare la corrispondenza,
     * quindi non neghiamo (altrimenti ogni Collection raw, comprese tutte quelle di JHotDraw,
     * fallirebbe sempre).
     *
     * <p>Perché questo controllo esiste: senza di esso, un campo {@code List<Point>} soddisfa
     * ugualmente un controllo "ha un campo Collection" pensato per riconoscere {@code
     * List<Observer>} — un falso match strutturale possibile ogni volta che una classe ha,
     * per un motivo estraneo al pattern, un'altra Collection generica nei suoi campi.
     */
    public static boolean isCollectionOf(FieldInfo field, String expectedElementTypeFqn) {
        if (!KNOWN.contains(field.getTypeName())) {
            return false;
        }
        String elementType = field.getGenericElementTypeName();
        if (elementType == null) {
            return true;
        }
        return TypeHierarchy.isAssignable(elementType, expectedElementTypeFqn);
    }
}
