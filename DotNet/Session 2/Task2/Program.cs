namespace Task2
{
    internal class Program
    {
        static void Main(string[] args)
        {
            // TODO: Create your complete football application

            Console.WriteLine("🏆 FOOTBALL MANAGEMENT SYSTEM");
            Console.WriteLine("================================\n");
            #region TODO1:
            // 1. Create a team
            // 2. Add players of different positions
            /*
             * // ============================================
            // 1. OOP - Creating Players (Encapsulation)
            // ============================================
               Console.WriteLine("--- 1. CREATING PLAYERS (OOP) ---\n");
            
            var salah = new Forward("Mohamed Salah", 10, 31, 32);
            var trezeguet = new Forward("Trezeguet", 7, 29, 15);
            var hegazi = new Defender("Ahmed Hegazi", 6, 32, 45, 30);
            var marmoush = new Forward("Omar Marmoush", 14, 24, 12);
            var elShenawy = new Goalkeeper("Mohamed El-Shenawy", 1, 34, 12);
            
            // ============================================
            // 2. OOP - Inheritance & Polymorphism
            // ============================================
            Console.WriteLine("--- 2. POLYMORPHISM ---\n");
            
            List<FootballPlayer> players = new List<FootballPlayer> { salah, trezeguet, hegazi, marmoush, elShenawy };
            
            foreach (var player in players)
            {
                player.Play();  // Different behavior for each!
                player.Train(); // Different behavior for each!
                Console.WriteLine();
            }
            
            // ============================================
            // 3. TEAM CREATION
            // ============================================
            Console.WriteLine("--- 3. CREATING TEAM ---\n");
            
            var team = new Team("Al Ahly SC", "Marcel Koller");
            team.AddPlayer(salah);
            team.AddPlayer(trezeguet);
            team.AddPlayer(hegazi);
            team.AddPlayer(marmoush);
            team.AddPlayer(elShenawy);
            
            team.DisplaySquad();
             */
            #endregion

            #region TODO2:

            // 3. Use indexers to access players
            // ============================================
            //  INDEXERS - Accessing Players
            // ============================================
            //Console.WriteLine("--- 4. INDEXERS ---\n");

            //// By position
            //Console.WriteLine($"Player at index 0: {team[0]?.Name}");

            //// By jersey number
            //var playerByNumber = team[10, true];
            //Console.WriteLine($"Player with jersey #10: {playerByNumber?.Name}");

            //// By name
            //var playerByName = team["Salah"];
            //Console.WriteLine($"Player named 'Salah': {playerByName?.Name}");

            //// By position and stat
            //int forwardGoals = team["Forward", "Goals"];
            //Console.WriteLine($"Total goals by forwards: {forwardGoals}");

            // 4. Use generics for specialized collections
            // ============================================
            //  GENERICS - Squad and Stats
            // ============================================
            //Console.WriteLine("\n--- 5. GENERICS ---\n");

            //var forwardSquad = new Squad<Forward>();
            //forwardSquad.AddPlayer(salah);
            //forwardSquad.AddPlayer(trezeguet);
            //forwardSquad.AddPlayer(marmoush);
            //forwardSquad.DisplayAll();

            //var forwardStats = new StatsCalculator<Forward>();
            //var allForwards = forwardSquad.GetAll();
            //Console.WriteLine($"Forward average goals: {forwardStats.CalculateAverageGoals(allForwards)}");
            #endregion
            #region TODO3:
            // 5. Use delegates for filtering and reporting
            // ============================================
            // 6. DELEGATES - Filtering
            // ============================================
            //Console.WriteLine("\n--- 6. DELEGATES ---\n");

            //var reportGenerator = new TeamReportGenerator();

            //// Named method
            //reportGenerator.GenerateReport(team.ToList(), "Star Players", IsStarPlayer);

            //// Anonymous method
            //reportGenerator.GenerateReport(team.ToList(), "Veteran Players",
            //    delegate (FootballPlayer p) { return p.Age >= 30; });

            //// Lambda expression
            //reportGenerator.GenerateReport(team.ToList(), "Injured Players",
            //    p => p.IsInjured);

            //// ============================================
            //// 7. GENERIC DELEGATES - Func, Action, Predicate
            //// ============================================
            //Console.WriteLine("\n--- 7. GENERIC DELEGATES ---\n");

            //var analyzer = new TeamAnalyzer();

            //// Func - Calculate total goals
            //int totalGoals = analyzer.CalculateTeamStat(team.ToList(), p => p.Goals);
            //Console.WriteLine($"Total team goals: {totalGoals}");

            //// Action - Apply operation to all
            //Console.WriteLine("\nApplying training to all players:");
            //analyzer.ApplyToAllPlayers(team.ToList(), p => p.Train());

            //// Predicate - Find players
            //var youngPlayers = analyzer.FindPlayers(team.ToList(), p => p.Age < 25);
            //Console.WriteLine($"\nYoung players (<25): {youngPlayers.Count}");

            // 6. Use extension methods for enhanced functionality

            // ============================================
            // 8. EXTENSION METHODS
            // ============================================
            //  Console.WriteLine("\n--- 8. EXTENSION METHODS ---\n");

            // Player extensions
            //foreach (var player in team)
            //{
            //    Console.WriteLine($"{player.Name}: {player.GetRating()}");
            //    if (player.IsStar())
            //    {
            //        player.PrintStats();
            //        Console.WriteLine($"Value: ${player.GetValue():F2}\n");
            //    }
            //}

            //// Team extensions
            //team.PrintTeamReport();
            #endregion

            #region TODO4
            // 8. Simulate a match with events
            //var match = new Match();
            //var commentator = new MatchCommentator();
            //var fans = new Fans("Al Ahly");
            //var coach = new Coach("Koller");

            //// Subscribe to events
            //match.GoalScored += commentator.OnGoalScored;
            //match.GoalScored += fans.OnGoalScored;
            //match.GoalScored += coach.OnGoalScored;

            //// Run the match
            //match.StartMatch();
            //match.ScoreGoal("Salah");
            //match.InjurePlayer("Hegazi");
            //match.ScoreGoal("Trezeguet");
            //match.EndMatch()
            #endregion

            // All OOP principles should be demonstrated!
        }
    }
    public abstract class FootballPlayer
    {

        #region TODO1: Implement with proper encapsulation
        // - Name (string)
        // - JerseyNumber (int)
        // - Age (int)
        // - IsInjured (bool)
        // - Goals (int)
        // - Assists (int)

        // TODO1: Add constructor
        // TODO1: Add abstract Play() method
        // TODO1: Add virtual Train() method
        // TODO1: Add methods for Injure(), Recover(), ScoreGoal(), MakeAssist()
        #endregion
    }
    #region TODO1: Create Goalkeeper class

    // - Inherits from FootballPlayer
    // - Add CleanSheets property
    // - Override Play() and Train()
    #endregion


    #region TODO1: Create Defender class

    // - Inherits from FootballPlayer
    // - Add Tackles, Clearances properties
    // - Override Play() and Train()
    #endregion

    #region TODO1: Create Midfielder class

    // - Inherits from FootballPlayer
    // - Add Passes, KeyPasses properties
    // - Override Play() and Train()
    #endregion

    #region  TODO1: Create Forward class

    // - Inherits from FootballPlayer
    // - Add ShotsOnTarget property
    // - Override Play() and Train()
    #endregion
    //======================================+=============================================

    //======================================+=============================================

    public class Team
    { 
        #region TODO1:
    
    // - Name (string)
    // - Players (List<FootballPlayer>)
    // - CoachName (string)

    // TODO1: Add methods
    // - AddPlayer(FootballPlayer player)
    // - RemovePlayer(int jerseyNumber)
    // - DisplaySquad()
    // - CalculateAverageAge()
    // - GetTotalGoals()
    // - GetTopScorer()
    #endregion
     
    #region TODO2: Implement indexer by position (int)

    //public FootballPlayer this[int index]
    //{
    //    get { /* Return player at index */ }
    //    set { /* Set player at index */ }
    //}
    #endregion

    #region  Assignment: Implement indexer by other
    //public FootballPlayer this[int jerseyNumber, bool byNumber]
    //{
    //    get { /* Return player by jersey number */ }
    //}

    //// Assignment: Implement indexer by name (string)
    //public FootballPlayer this[string name]
    //{
    //    get { /* Return player by name */ }
    //}

    //// Assignment: Implement indexer by position and stat (bonus challenge)
    //public int this[string position, string stat]
    //{
    //    get { /* Return total stat for position */ }
    //}
    #endregion
    }

//======================================+=============================================

//======================================+=============================================
// 
#region TODO2: Create generic Squad<T> where T : FootballPlayer
public class Squad<T>
    {
        // TODO3: Implement generic collection
        // - Private List<T> _players
        // - AddPlayer(T player)
        // - RemovePlayer(int jerseyNumber)
        // - GetPlayersByPosition(string position) (filter by player type)
        // - GetTopScorers(int count)
        // - GetAverageRating()
    }
    #endregion 

    // 
    #region Assignmnet: Create StatsCalculator<T> where T : FootballPlayer
    public class StatsCalculator<T> 
    {
        // TODO: Implement methods
        // - CalculateAverageGoals(List<T> players)
        // - CalculateAverageAge(List<T> players)
        // - GetBestPlayer(List<T> players)
        // - GetWorstPlayer(List<T> players)
    }

    #endregion

    // TODO3: Declare delegate for player filtering  PlayerFilter(FootballPlayer player);


    
    #region TODO3: Create PlayerFilterExtensions class with methods
    public static class PlayerFilterExtensions
    {
        // TODO: Create filter methods
        // - IsStarPlayer() (Goals > 20)
        // - IsVeteran() (Age > 30)
        // - IsInjured() (IsInjured == true)
        // - IsGoalkeeper() (is Goalkeeper)
        // - HasMinimumGoals(int minGoals)
        // - ByPosition(string position)
    }
    #endregion 

    // 
    #region TODO3: Create TeamReportGenerator class
    public class TeamReportGenerator
    {
        // TODO3: Implement GenerateReport method
        // Takes: List<FootballPlayer>, string title, PlayerFilter filter
        // Displays: Players matching the filter with their stats
    }
    #endregion

    //
    #region Assignment:Create methods using Func, Action, Predicate
    //public class TeamAnalyzer
    //{
    //    // TODO: Method using Func to calculate team stat
    //    public int CalculateTeamStat(...)
    //    {
    //        // Returns sum of selected stat
    //    }

    //    // TODO: Method using Action to perform operation
    //    public void ApplyToAllPlayers(...)
    //    {
    //        // Applies operation to all players
    //    }

    //    // TODO: Method using Predicate to filter
    //    public List<FootballPlayer> FindPlayers(...)
    //    {
    //        // Returns players matching predicate
    //    }
    //}
    #endregion

    // 
    #region Assignment: Create static class ListExtensions
    public static class ListExtensions
    {
        // TODO: Add methods:
        // - Shuffle<T>(this List<T> list)
        // - GetTop<T>(this List<T> list, int count, Func<T, int> selector)
        // - GetRandom<T>(this List<T> list, int count)
    }
    #endregion


    #region Assignmnet: Make Team class implement IEnumerable<FootballPlayer>
    //public class Team : IEnumerable<FootballPlayer>
    //{
    //    // TODO: Implement GetEnumerator with yield
    //    // TODO: Implement custom iteration methods:
    //    // - GetStarPlayers() - Only star players
    //    // - GetInjuredPlayers() - Only injured players
    //    // - GetPlayersByAgeRange(int min, int max)
    //    // - GetPlayersSortedByGoals() - Sorted by goals
    //    // - GetPlayersByPosition(string position) - Filter by position
    //}
    #endregion

    // 
    #region TODO4: Create Match class with events
    public class Match
    {
        // TODO: Declare events
        // - GoalScored (string scorer)
        // - PlayerInjured (string player)
        // - RedCard (string player)
        // - MatchStarted
        // - MatchEnded
        // - Substitution (string playerIn, string playerOut)

        // TODO: Implement methods
        // - StartMatch()
        // - ScoreGoal(string scorer)
        // - InjurePlayer(string player)
        // - ShowRedCard(string player)
        // - EndMatch()
        // - MakeSubstitution(string playerIn, string playerOut)
    }
    // TODO4: Create observers/listeners
    public class MatchCommentator
    {
        // TODO4: Subscribe to events and display commentary
    }

    public class Fans
    {
        // TODO4: Subscribe to events and display fan reactions
    }

    public class Coach
    {
        // TODO4: Subscribe to events and display coach reactions
    }

    public class VAR
    {
        // TODO4: Subscribe to events and display VAR reviews
    }
    #endregion
}

