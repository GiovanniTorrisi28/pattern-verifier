package com.patternverifier.mediator.correct;

import com.patternverifier.annotations.GoFMediator;

import java.util.ArrayList;
import java.util.List;

// ConcreteMediator: è l'unico a conoscere tutti i partecipanti.
@GoFMediator(mediatorInterface = ChatMediator.class,
             colleagues = {UserColleague.class, BotColleague.class})
public class ChatRoom implements ChatMediator {
    private final List<Participant> participants = new ArrayList<>();

    public void register(Participant participant) {
        participants.add(participant);
    }

    @Override
    public void send(String message, Participant sender) {
        for (Participant p : participants) {
            if (p != sender) {
                p.receive(message);
            }
        }
    }
}
