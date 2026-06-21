package com.patternverifier.abstractfactory.wrong;

import com.patternverifier.abstractfactory.correct.Button;

// VIOLAZIONE 2: ha solo 1 factory method — manca quello per TextField
public interface SingleMethodFactory {
    Button createButton();
}
