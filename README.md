# pattern-verifier

Libreria JUnit 5 per verificare che le classi Java implementino correttamente i design pattern della Gang of Four (GoF), tramite analisi del bytecode con ASM.

Il progetto è lo strumento sviluppato per una tesi di laurea magistrale in Informatica (Università degli Studi di Catania). Non fa detection — non indovina quali pattern siano presenti in un codice sconosciuto — ma conformance checking: parte da una dichiarazione esplicita di intento e verifica che il codice la rispetti.

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

Se la classe non rispetta la struttura del pattern dichiarato, il test fallisce con un messaggio che elenca tutte le violazioni trovate:

```
SocketAdapter: violazione pattern Adapter (Adaptee=LegacySocket, Target=ModernSocket)
  - SocketAdapter non delega mai all'Adaptee LegacySocket — l'Adapter (object adapter)
    deve invocare metodi dell'Adaptee per realizzare la traduzione
```

## Pattern supportati

18 dei 23 pattern GoF sono implementati. I 5 esclusi sono quelli le cui proprietà caratterizzanti non sono verificabili staticamente: la loro definizione riguarda il comportamento a runtime o l'intento del progettista, non la struttura delle classi.

| Creazionali | Strutturali | Comportamentali |
|---|---|---|
| Singleton | Adapter | Strategy |
| Factory Method | Proxy | Observer |
| Abstract Factory | Decorator | Command |
| Builder | Composite | Template Method |
| Prototype | Bridge | State |
| | | Chain of Responsibility |
| | | Visitor |
| | | Mediator |

## Valutazione su codice reale

Lo strumento è stato valutato su **JHotDraw 5.1**, framework storico co-progettato da Erich Gamma come vetrina dei pattern GoF, usando come oracolo esterno il catalogo peer-reviewed **P-MARt**. Ogni classe dichiarata dall'oracolo è un caso di test indipendente: 130 casi in tutto.

| Conteggio | Classi conformi | Classi testate | TPR |
|---|---|---|---|
| Solo oracolo esterno (P-MARt) | 78 | 119 | 65,5% |
| Oracolo esterno + analisi manuale | 79 | 130 | 60,8% |

Il dato interessante non è la percentuale ma la sua scomposizione. Le 51 classi non conformi non dicono tutte la stessa cosa:

- **38** — il pattern è realmente implementato, ma devia dal canone su una proprietà specifica; qui il fallimento è un successo diagnostico;
- **9** — l'attribuzione del ruolo nell'oracolo è sbagliata, e lo strumento sta esponendo un errore della fonte, non un difetto del codice;
- **3** — la relazione fra le classi esiste, ma è di natura diversa da quella prescritta dal pattern;
- **1** — un ruolo che richiede un campo di istanza risulta attribuito a un'interfaccia, dove il fallimento è strutturalmente garantito.

Ogni caso non conforme è stato verificato singolarmente contro il sorgente di JHotDraw, confrontando il messaggio prodotto dallo strumento con il codice che lo ha causato.

## Usare il tool nel proprio progetto

`pattern-verifier` non è pubblicato su Maven Central: va installato nel repository Maven locale a partire dal sorgente, poi referenziato come qualsiasi altra dipendenza.

**1. Clonare e installare il tool**:
```bash
git clone https://github.com/GiovanniTorrisi28/pattern-verifier.git
cd pattern-verifier
.\mvnw.cmd install    # Windows
./mvnw install         # macOS / Linux
```
Compila il tool, esegue i suoi 171 test interni e pubblica il jar in `~/.m2/repository/com/patternverifier/pattern-verifier/` — disponibile da quel momento a qualsiasi progetto Maven sulla stessa macchina.

**2. Aggiungere la dipendenza nel proprio progetto** (`pom.xml`):
```xml
<dependency>
    <groupId>com.patternverifier</groupId>
    <artifactId>pattern-verifier</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

**3. Scrivere un test JUnit 5** usando il DSL fluente (vedi sopra) oppure le annotazioni:
```java
@GoFAdapter(adaptee = LegacySocket.class, target = ModernSocket.class)
public class SocketAdapter implements ModernSocket { ... }

@Test
void patternsShouldBeCorrectlyImplemented() {
    PatternAnnotationScanner.verify(SocketAdapter.class);
}
```

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
[INFO] Tests run: 171, Failures: 0, Errors: 0, Skipped: 1
[INFO] BUILD SUCCESS
```
(Lo `Skipped: 1` è un test `@Disabled` con spiegazione — un limite fondamentale di ArchUnit documentato nel confronto con quello strumento, non un test rotto.)

Per eseguire i test di un singolo pattern:
```bash
.\mvnw.cmd test -Dtest="SingletonVerifierTest"
.\mvnw.cmd test -Dtest="ObserverVerifierTest"
```

## Riprodurre la valutazione su JHotDraw 5.1

Il ground truth (quale classe implementa quale pattern) viene dal catalogo P-MARt, repository di
micro-architetture di pattern curato dal Ptidej Team e usato come riferimento nella letteratura
sul rilevamento dei design pattern. La copia usata per questa valutazione è in
[docs/pmart_design_pattern_list_v1.2.xml](docs/pmart_design_pattern_list_v1.2.xml), insieme al
paper originale che la descrive.

JHotDraw 5.1 non è su Maven Central (è un progetto del 1997 non più mantenuto): il sorgente va
scaricato, compilato e installato in locale prima di eseguire i test di valutazione. Questi passi
servono **solo per la valutazione** — i 171 test principali del tool (sezione sopra) girano con
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
# Windows
.\mvnw.cmd install:install-file -Dfile=target/jhotdraw-5.1.jar -DgroupId=ch.ifa.draw -DartifactId=jhotdraw -Dversion=5.1 -Dpackaging=jar

# macOS / Linux
./mvnw install:install-file -Dfile=target/jhotdraw-5.1.jar -DgroupId=ch.ifa.draw -DartifactId=jhotdraw -Dversion=5.1 -Dpackaging=jar
```

**5. Eseguire i test di valutazione** (attivando il profilo `jhotdraw-evaluation`):
```bash
.\mvnw.cmd test -P jhotdraw-evaluation
```

Output atteso:
```
[INFO] Tests run: 303, Failures: 0, Errors: 0, Skipped: 1
[INFO] BUILD SUCCESS
```

Il profilo aggiunge la dipendenza `ch.ifa.draw:jhotdraw:5.1` (risolta dal repository Maven locale
dopo il passo 4) e la cartella `src/test/java-jhotdraw/`, tenuta separata da `src/test/java/` in
modo che la build di default non richieda mai il corpus JHotDraw. Contiene
`InheritanceAwareAnalysisValidationTest` (prova di regressione sull'analisi inheritance-aware) e
`JHotDrawPatternEvaluationTest` (la valutazione completa sui 130 casi).

I test di valutazione passano quando lo strumento produce l'esito atteso, che per le classi non
conformi è il fallimento della verifica: sono scritti con `assertThrows` e controllano anche il
contenuto del messaggio di violazione, non solo che l'eccezione venga sollevata.

## Struttura del progetto

```
src/main/java/com/patternverifier/
├── PatternAssertions.java          # punto di ingresso del DSL
├── PatternAnnotationScanner.java   # punto di ingresso dell'API a annotazioni
├── core/                           # lettura bytecode ASM + modello interno
│   ├── ClassAnalyzer.java                      # analisi inheritance-aware di una classe
│   ├── ClassMetadata.java
│   ├── FieldInfo.java
│   ├── MethodInfo.java
│   ├── TypeHierarchy.java                      # assegnabilità fra tipi
│   ├── CollectionTypes.java                    # collezioni e argomento generico
│   ├── MethodInvocationAnalyzer.java           # invocazioni, lambda e method reference
│   ├── SelfReturnAnalyzer.java                 # metodi che restituiscono this (Builder)
│   ├── InternalFactoryAssignmentAnalyzer.java  # selezione interna di Strategy/State
│   └── TemplateMethodBodyAnalyzer.java         # corpo del metodo modello
├── verifiers/                      # un verifier per pattern
├── assertions/                     # classi intermedie del DSL (XxxAssert)
└── annotations/                    # annotazioni @GoFXxx

src/test/java/com/patternverifier/
└── <nomepattern>/
    ├── <NomePattern>VerifierTest.java
    ├── correct/                    # fixture implementate correttamente
    └── wrong/                      # fixture con violazioni intenzionali

src/test/java-jhotdraw/com/patternverifier/jhotdraw/
├── InheritanceAwareAnalysisValidationTest.java
└── JHotDrawPatternEvaluationTest.java   # valutazione su JHotDraw 5.1 (profilo jhotdraw-evaluation)
```

## Tecnologie

- [ASM 9.7](https://asm.ow2.io/) — analisi del bytecode (legge i file `.class` senza eseguirli)
- [JUnit 5.11](https://junit.org/junit5/) — integrazione con i test tramite `AssertionError`
