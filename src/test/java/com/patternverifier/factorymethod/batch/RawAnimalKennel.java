package com.patternverifier.factorymethod.batch;

import java.util.Vector;

/**
 * Collezione raw (senza generics), come {@code Vector handles()} in JHotDraw 1997: nel bytecode
 * non esiste attributo Signature da cui leggere il tipo elemento, quindi il tool non può
 * confermare che la collezione contenga Animal — deve essere trattata come un mancato match, non
 * come un match implicito.
 */
public abstract class RawAnimalKennel {
    @SuppressWarnings("rawtypes")
    public abstract Vector createAnimals();
}
