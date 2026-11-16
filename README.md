## Requirements
- JDK 25
- Maven 3.9+ (or compatible)

## Run
```bash
mvn clean javafx:run
```

## Test
```bash
mvn test
```

# Major Software Components & Responsibilities
1. Model & Game Logic Layer
    - Card: An immutable representation of a playing card defined by a Rank (values 2 through 14, Ace high) and a Suit (Clubs, Diamonds, Hearts, Spades).

    - Deck: Encapsulates a standard 52-card deck with operations to reset all cards, shuffle, and deal the top card, throwing an IllegalStateException if drained.

    - Dealer: Manages the deck instance, handles card shuffling, and deals discrete 3-card hands to players and the house.

    - Player: Encapsulates state for an individual seat: active 3-card hand, current anteBet, pairPlusBet, playBet, cumulative totalWinnings, and round status (folded).

    - ThreeCardLogic: A stateless utility class containing pure mathematical poker logic:

        - evalHand: Categorizes a hand into HandRank (Straight Flush, Three of a Kind, Straight, Flush, Pair, High Card).

        - evalPPWinnings: Determines Pair Plus side-bet payouts (40:1, 30:1, 6:1, 3:1, 1:1, or 0).

        - compareHands: Resolves competitive outcomes (CMP_PLAYER_WINS, CMP_DEALER_WINS, CMP_TIE) by comparing rank hierarchies and evaluating kickers/tiebreakers.

        - dealerQualifies: Verifies if the dealer holds at least Queen-high or any made hand.

        - GameService: The game coordinator mediating between the UI controllers and the domain model. It tracks game phases (PLACE_WAGERS, PLAY_OR_FOLD, ROUND_OVER, GAME_OVER), validates wagering boundaries ($5 to $25), processes play/fold decisions, settles rounds, calculates net bankrolls, and enforces table bankruptcies without referencing any JavaFX UI elements.

2. Controllers & View Layer
    - WelcomeController: Handles main menu entry, initiates title pulsing animations, and routes scene transitions to the game table or the interactive tutorial.
    - PlayController: Binds to play.fxml. Translates user button presses and text input into GameService calls, triggers UI animations (dealing, card flips, win/loss balance pulses), and updates table feedback labels based on the active round state.  
    - TutorialController: Drives the 6-step interactive visual tutorial overlay, highlighting controls matching each stage of play.   
    - ThemeManager: Manages the night/day style theme state and toggles the CSS night style class across all loaded scenes.


## Purpose of Each Required Screen
1. Screen 1: Welcome / Main Menu: The primary landing point of the application. It greets the user with an animated title and provides clear paths to:
    - Play: Navigates straight to the active 2-player table.
    - Tutorial: Launches the step-by-step interactive onboarding guide.  
    - Quit: Exits the JavaFX runtime.

2. Screen 2: Game Play Screen: The primary operational screen supporting two independent players competing simultaneously against a single dealer. It displays:   
    - Both players' cards and bankrolls, along with the dealer's face-down cards.
    - Independent betting boxes for Ante and optional Pair Plus wagers ($5–$25) with stepper buttons and direct text parsing.
    - Action buttons (PLAY and FOLD) enabled only during decision phases.   
    - Central table banners displaying the current phase and instructive guidance for user actions.Real-time payout feedback and cumulative winnings after dealer reveal.

3. Screen 3: Tutorial Walkthrough Screen: A guided simulation of the table layout designed from usability feedback. Instead of presenting a dense wall of text, it takes players through a 6-step chronological round (Ante/Pair Plus betting -> dealing -> folding/playing -> dealer reveal -> rules lookup -> navigation) using yellow callout cards and glowing UI highlights.

4. Information Overlay: A two-page dialog accessible in the top-right corner of every screen. Page 1 details Ante, Pair Plus, dealing sequences, and dealer qualification rules. Page 2 lists hand ranking hierarchies and the Pair Plus payout schedule.

## Purpose of Each Animation
1. Title Pulse Animation:
    - Mechanism: An indefinite ScaleTransition pulsing the title between 1.0x and 1.08x every 1.5 seconds.
    - Purpose: Enhances presentation quality and signals that the application is active and responsive.
2. Card Dealing Cascade:
    - Mechanism: Combines a TranslateTransition (moving cards down onto the table) and a FadeTransition (opacity 0 to 1) staggered per card across Player 1 and Player 2.
    - Purpose: Simulates physical dealing from the deck. Disabling decision buttons until the final card lands prevents players from prematurely clicking inputs before their hand is visually registered.
3. Dealer Card Flip:
    - Mechanism: A two-phase sequential ScaleTransition shrinking the card horizontally, swapping the graphic from a card-back ? to the revealed rank and suit, and expanding X from 0 to 1, staggered per card.
    - Purpose: Mimics a dealer turning cards face up one-by-one. It builds suspense around dealer and hand resolution.
4. Winnings/Loss Total Pulse:
    - Mechanism: Temporarily styles the player balance green for net wins or red for net losses while applying a 1.35x ScaleTransition bounce.
    - Purpose: Provides immediate visual reinforcement of the financial outcome of a round without requiring users to manually calculate differences.

## Changes Made as a Result of Part 1 Usability Testing
1. Elimination of Mandatory Popups:
    - Initial Problem: The first wireframe launched a mandatory tutorial dialog every time a user started a new game session, frustrating returning players.   
    - Revision: The tutorial was separated into an on-demand option on the main menu, and an accessible i information button was placed permanently in the top-right corner of all screens.
2. Visible Bet Input: 
    - Initial Problem: Bets were hard to work with as buttons were small, which people found tedious when adjusting between $5 and $25.
    - Revision: Increased size of buttons and visibility of betting amount.
3. Seat Ambiguity and Visual Differentiation:
    - Initial Problem: Users were confused about which side of the table belonged to which player and where actions were supposed to be taken.
    - Revision: Distinct visual badges were introduced for Player 1 (Plus avatar) and Player 2 (Star avatar) with dedicated bankroll trackers and color-coded status cues.

## Design Reflection Questions

1. Why did you separate game logic from the FXML controllers?
    - Separating domain logic from FXML controllers increases modularity and the Model-View-Controller (MVC) architectural pattern. JavaFX controllers are inherently coupled to the UI lifecycle, user input events, scene graph rendering, and animations. If hand evaluation algorithms, tiebreaker math, deck state, or bankroll calculations were embedded directly in PlayController, several issues would arise. Such as untestable logic and easily breakable code.
2. What usability problems did other students identify?
    - Flow friction: testers expressed frustration with a recurring tutorial question popping up whenever a game started.
    - Control visibility: Adjusting wagers was hard to see and needed some sort of indication or ease of access.
    - Seat confusion: The symmetrical layout of two player stations across a single screen created confusion regarding which side represented Player 1 versus Player 2, and whose turn it was to act.

3. Which wireframe revisions most improved the user experience?
    - The most impactful revision was definitely the first one. This is because it had the most criticisms and room for improvement which added things like the universal i information button and distinct player identification badges & action states.
4. How do the animations help users understand game events?
    - Dealing transitions like staggering cards flying into player hands mirrors the physical aspect of a casino table. I think this makes it overall more fun. It also communicates that cards are actively being dealt and prevents input race conditions by disabling actions until the deal is complete.
    - The dealer's cards flip sequentially rather than appearing instantly. This gives players time to inspect each card and visually compare the cards to their own hand.
    - Shifting bankroll labels to green or red with an expanding scale pulse gives immediate feedback on round outcomes, making it clear whether a player won or lost chips.
5. How could the interface be redesigned without changing the game rules?
    - Instead of displaying both players simultaneously on a shared desktop screen, the UI could use a split-stage or turn-swapping view where only the active player's cards and controls are revealed, preventing opponents from seeing each other's hands during local pass-and-play. Also,the numeric text fields and stepper buttons could be replaced with visual casino chip denominations ($5, $10, $25 chips) that players drag and drop onto designated Ante and Pair Plus felt betting circles.
6. How does your architecture make the application easier to test and maintain?
    ThreeCardLogic contains no references to JavaFX, allowing all card valuations, edge-case tiebreakers, and Pair Plus multipliers to be tested using standard JUnit 5 suites in milliseconds. PlayController acts solely as an event listener and visual renderer. If the visual representation changes (for example, switching from text card representations to image-based SVG assets), only the FXML and controller view code need updates; the core game engine remains untouched.
    
    Adding new game variations, adjusting betting limits, or modifying Pair Plus paytables requires modifying only isolated constants in GameService and ThreeCardLogic, preventing breaks in the UI. Turn transitions, wager validation, and bankroll tracking are coordinated by GameService. This logic can be unit-tested using mock players and hands without needing JavaFX scene graph components. Overall, having seperated game logic allows you to make changes in one class without breaking every other thing other classes do.

## Team Members & Contributions
## Christopher Moreno: 
Usability testing and wireframe evaluations, peer interviews, UI wireframe revisions, FXML scene structures (welcome.fxml, play.fxml, tutorial.fxml), tutorial walkthrough flow, Information (i) integration, and project documentation / design reflection.   
## Jorge Urias:
 Full core model and game logic implementation, event-driven controller logic and transitions in PlayController, ThemeManager, automated unit testing suites (ThreeCardLogicTest, DeckTest, DealerTest), and CSS styling.