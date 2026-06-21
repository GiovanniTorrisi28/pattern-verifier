# pattern-verifier

Libreria JUnit 5 per verificare che le classi Java implementino correttamente i design pattern della Gang of Four (GoF), tramite analisi del bytecode con ASM.

## Cosa fa

`pattern-verifier` fornisce un DSL fluente da usare all'interno dei test JUnit per dichiarare quale pattern una classe intende implementare e verificare che la struttura del codice lo rispetti:

```java
@Test
void socketAdapterShouldPass() {
    PatternAssertions.assertThat(SocketAdapter.class)
        .implementsAdapter()
        .fromAdaptee(LegacySocket.class)
        .toTarget(ModernSocket.class);
}
```

Se la classe non rispetta la struttura del pattern dichiarato, il test fallisce con un messaggio che elenca tutte le violazioni trovate.

## Pattern supportati

16 dei 23 pattern GoF sono implementati. I 7 esclusi non hanno proprietà strutturali verificabili staticamente; la motivazione per ciascuno è documentata in [docs/formalizzazione_pattern.md](docs/formalizzazione_pattern.md).

| Creazionali | Strutturali | Comportamentali |
|---|---|---|
| Singleton | Adapter | Strategy |
| Factory Method | Proxy | Observer |
| Abstract Factory | Decorator | Command |
| Builder | Composite | Template Method |
| | Bridge | State |
| | | Chain of Responsibility |
| | | Visitor |

## Requisiti

- **Java 17+** (unico prerequisito — Maven non deve essere installato)

## Eseguire i test

```bash
# Windows
.\mvnw.cmd test

# macOS / Linux
./mvnw test
```

Al primo avvio Maven Wrapper scarica Maven e le dipendenze automaticamente (circa 1 minuto, richiede connessione internet). Dalle esecuzioni successive impiega pochi secondi.

Output atteso:
```
[INFO] Tests run: 105, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Per eseguire i test di un singolo pattern:
```bash
.\mvnw.cmd test -Dtest="SingletonVerifierTest"
.\mvnw.cmd test -Dtest="ObserverVerifierTest"
```

## Struttura del progetto

```
src/main/java/com/patternverifier/
├── PatternAssertions.java          # punto di ingresso del DSL
├── core/                           # lettura bytecode ASM + modello interno
│   ├── ClassAnalyzer.java
│   ├── ClassMetadata.java
│   ├── FieldInfo.java
│   └── MethodInfo.java
├── verifiers/                      # un verifier per pattern
└── assertions/                     # classi intermedie del DSL (XxxAssert)

src/test/java/com/patternverifier/
└── <nomepattern>/
    ├── <NomePattern>VerifierTest.java
    ├── correct/                    # fixture implementate correttamente
    └── wrong/                      # fixture con violazioni intenzionali
```

## Documentazione

| Documento | Contenuto |
|---|---|
| [docs/architettura.md](docs/architettura.md) | Architettura interna — pipeline a 4 livelli, motivazioni di design |
| [docs/formalizzazione_pattern.md](docs/formalizzazione_pattern.md) | Proprietà verificate per ogni pattern, pattern esclusi con motivazione |
| [docs/analisi_tesi.md](docs/analisi_tesi.md) | Stato dell'arte, approccio, note di design dell'API |
| [docs/decisioni.md](docs/decisioni.md) | Log delle decisioni architetturali e relative motivazioni |
| [docs/piano_lavoro.md](docs/piano_lavoro.md) | Fasi di sviluppo e avanzamento |

## Tecnologie

- [ASM 9.7](https://asm.ow2.io/) — analisi del bytecode (legge i file `.class` senza eseguirli)
- [JUnit 5.11](https://junit.org/junit5/) — integrazione con i test tramite `AssertionError`
