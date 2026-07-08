package com.patternverifier.regressionfixes;

import com.patternverifier.annotations.GoFObserver;
import com.patternverifier.annotations.GoFSingleton;

/**
 * Classe con <b>due</b> annotazioni @GoFXxx contemporaneamente, entrambe violate di proposito.
 *
 * Regressione per il fix "PatternAnnotationScanner verifica tutte le annotazioni": prima della
 * correzione lo scanner si fermava alla prima annotazione nell'ordine di dispatch (Singleton),
 * ignorando silenziosamente la seconda (Observer). Ora deve segnalare le violazioni di ENTRAMBI
 * i ruoli.
 *
 * Violazioni attese:
 * - Singleton: costruttore pubblico, nessun campo static dell'istanza, nessun getter static.
 * - Observer: nessuna collezione di observer, nessun metodo di registrazione/notifica.
 */
@GoFSingleton
@GoFObserver(observer = ChangeListener.class)
public class DualRoleClass {

    public DualRoleClass() {
        // costruttore pubblico: viola Singleton
    }
}
