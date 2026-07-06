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
[INFO] Tests run: 139, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Per eseguire i test di un singolo pattern:
```bash
.\mvnw.cmd test -Dtest="SingletonVerifierTest"
.\mvnw.cmd test -Dtest="ObserverVerifierTest"
```

## Riprodurre la valutazione su JHotDraw 5.1 (Fase 5)

Il progetto include una valutazione del tool su codice reale: **JHotDraw 5.1**, framework Java
storico progettato da Erich Gamma esplicitamente come vetrina dei design pattern GoF. Il ground
truth (quale classe implementa quale pattern) viene dal catalogo P-MARt ufficiale — vedi
[docs/ground_truth_jhotdraw_pmart.md](docs/ground_truth_jhotdraw_pmart.md) per la fonte e la sua
autorevolezza.

JHotDraw 5.1 non è su Maven Central (è un progetto del 1997 non più mantenuto): il sorgente va
scaricato, compilato e installato in locale prima di eseguire i test di valutazione. Questi passi
sono **necessari solo per la Fase 5** — i 139 test principali del tool (sezione sopra) girano con
`mvnw test` senza nulla di tutto questo: la dipendenza JHotDraw e i test che la usano sono isolati
nel profilo Maven `jhotdraw-evaluation` (attivo solo con `-P jhotdraw-evaluation`), non nella
build di default.

**1. Scaricare il sorgente di JHotDraw 5.1** (repository pubblico del Ptidej Team, licenza LGPL):
```bash
git clone --filter=blob:none --sparse https://github.com/ptidejteam/p-mart-Repository-Programs ../p-mart
cd ../p-mart && git sparse-checkout set "JHotDraw v5.1"
cd ../pattern-verifier
```

**2. Compilare i sorgenti** (compatibilità Java 8, encoding originale ISO-8859-1):
```bash
mkdir -p target/jhotdraw-build/classes
find "../p-mart/JHotDraw v5.1/src" -iname "*.java" | sed 's/.*/"&"/' > target/jhotdraw-build/sources.txt
javac --release 8 -encoding ISO-8859-1 -d target/jhotdraw-build/classes @target/jhotdraw-build/sources.txt
```

**3. Impacchettare in JAR**:
```bash
jar cf target/jhotdraw-5.1.jar -C target/jhotdraw-build/classes .
```

**4. Installarlo nel repository Maven locale**:
```bash
.\mvnw.cmd install:install-file -Dfile=target/jhotdraw-5.1.jar -DgroupId=ch.ifa.draw -DartifactId=jhotdraw -Dversion=5.1 -Dpackaging=jar
```

**5. Eseguire i test di valutazione** (attivando il profilo `jhotdraw-evaluation`):
```bash
.\mvnw.cmd test -P jhotdraw-evaluation
```

Il profilo aggiunge la dipendenza `ch.ifa.draw:jhotdraw:5.1` (risolta dal repository Maven locale
dopo il passo 4) e la cartella `src/test/java-jhotdraw/`, tenuta separata da `src/test/java/` in
modo che la build di default non richieda mai il corpus JHotDraw. Contiene per ora
`InheritanceAwareAnalysisValidationTest` (prova di regressione sull'analisi inheritance-aware);
`JHotDrawPatternEvaluationTest` (la valutazione completa di Fase 5) andrà nella stessa cartella.

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

src/test/java-jhotdraw/com/patternverifier/jhotdraw/
└── ...                              # test che dipendono da JHotDraw 5.1 (profilo jhotdraw-evaluation)
```

## Documentazione

| Documento | Contenuto |
|---|---|
| [docs/architettura.md](docs/architettura.md) | Architettura interna — pipeline a 4 livelli, motivazioni di design |
| [docs/formalizzazione_pattern.md](docs/formalizzazione_pattern.md) | Proprietà verificate per ogni pattern, pattern esclusi con motivazione |
| [docs/analisi_tesi.md](docs/analisi_tesi.md) | Stato dell'arte, approccio, note di design dell'API |
| [docs/decisioni.md](docs/decisioni.md) | Log delle decisioni architetturali e relative motivazioni |
| [docs/piano_lavoro.md](docs/piano_lavoro.md) | Fasi di sviluppo e avanzamento |
| [docs/ground_truth_jhotdraw_pmart.md](docs/ground_truth_jhotdraw_pmart.md) | Ground truth primario per la valutazione su JHotDraw 5.1 (catalogo P-MARt, fonte e autorevolezza) |
| [docs/jhotdraw51_class_role_index.md](docs/jhotdraw51_class_role_index.md) | Indice classe→ruolo per JHotDraw 5.1, con attribuzione della fonte |
| [docs/note_valutazione_jhotdraw.md](docs/note_valutazione_jhotdraw.md) | Rischi di falso negativo attesi per verifier, risultati e categorizzazione dei fallimenti sulla valutazione JHotDraw |
| [docs/jhotdraw_analisi_fallimenti.md](docs/jhotdraw_analisi_fallimenti.md) | Dettaglio classe-per-classe dei 48 casi non conformi: messaggio reale del tool + verifica sul sorgente |

## Tecnologie

- [ASM 9.7](https://asm.ow2.io/) — analisi del bytecode (legge i file `.class` senza eseguirli)
- [JUnit 5.11](https://junit.org/junit5/) — integrazione con i test tramite `AssertionError`
