/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael KÃ¶lling and David J. Barnes
 * @version 2011.07.31
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;

    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room aulaProgramacion, pasilloUno, aulaExamenes, balcon, hall, tejado, callejon, patio, calle, salonActos,
        pasilloDos, banyo, puertaPrincipal, secretaria, pasilloTres;

        // create the rooms
        aulaProgramacion = new Room("Estás en un aula llena de ordenadores, huele fuerte. Todo está oscuro pero ves una puerta");
        pasilloUno = new Room("Estás en un pasillo, hay varias puertas y una luz tenue entra por la ventana");
        aulaExamenes = new Room("Estás en un aula, hay una luz suave y no puedes ver todos los pupitres, pero ves una puerta al fondo");
        balcon = new Room("Estás en un balcon que no lleva a ninguna parte, date la vuelta");
        hall = new Room("Entras en una gran sala, tiene dos puertas mas la que se encuentra detras de ti, también hay una pequeña ventana que da a un tejadillo");
        tejado = new Room("Saltas al tejado, cerca tienes la ventana aunque puedes tirarte a un callejon oscuro, si lo haces no podras volver a subir");
        callejon = new Room("Esta oscuro, pero puedes ver una valla que da al patio");
        patio = new Room("Hay un poco mas de luz y ves una valla mas alta. La puerta esta cerrada");
        calle = new Room("Enhorabuena, saltando como un atleta olimpico has llegado a la calle y has escapado del instituto");
        salonActos = new Room("Estas en una sala enorme con forma de anfiteatro, tiene un proyector y otra salida. Cuidado con las sillas");
        pasilloDos = new Room("Estas en una sala estrecha, esta muy oscuro pero palpando las paredes puedes encontrar otras dos puertas");
        banyo = new Room("Entras en una sala llena de azulejos blancos, hay urinarios y grifos muy bajos, huele mal. Mejor vuelve por donde has venido");
        puertaPrincipal = new Room("Has llegado a la puerta principal. Coges una pantalla de ordenador y rompes la puerta, corre antes de que alguien te vea. \nHas destruido propiedad publica y huido exitosamente del instituto");
        secretaria = new Room("Iluminando la sala con tu telefono puedes ver cuadros con gente muerta colgando de las paredes. \nPor suerte son orlas y no algo especialmente siniestro, ves una puerta al otro lado de la habitacion y un monton de papeles apilados");
        pasilloTres = new Room("Ante ti hay una sala estrecha y oscura, deberían haber puesto ventanas, ante ti hay una puerta y en las paredes hay cuadros de los del Ikea");

        // initialise room exits N\E\S\W
        aulaProgramacion.setExits(pasilloUno, null, null, null);
        pasilloUno.setExits(hall, null, aulaProgramacion, aulaExamenes);
        aulaExamenes.setExits(null, pasilloUno, null, balcon);
        balcon.setExits(null, aulaExamenes, null, null);
        hall.setExits(pasilloTres, tejado, pasilloUno, salonActos);
        tejado.setExits(null, null, callejon, hall);
        callejon.setExits(tejado, null, patio, null);
        patio.setExits(callejon, calle, null, null);
        calle.setExits(null, patio, null, null);
        salonActos.setExits(pasilloDos, hall, null, null);
        pasilloDos.setExits(puertaPrincipal, secretaria, salonActos, banyo);
        banyo.setExits(null, pasilloDos, null, null);
        puertaPrincipal.setExits(null, null, pasilloDos, null);
        secretaria.setExits(null, null, pasilloTres, pasilloDos);
        pasilloTres.setExits(secretaria, null, hall, null);

        currentRoom = aulaProgramacion;  // start game outside
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        printLocationInfo();
    }

    private void printLocationInfo(){
        System.out.println(currentRoom.getDescription());
        System.out.print("Salidas: ");
        if(currentRoom.northExit != null) {
            System.out.print("north ");
        }
        if(currentRoom.eastExit != null) {
            System.out.print("east ");
        }
        if(currentRoom.southExit != null) {
            System.out.print("south ");
        }
        if(currentRoom.westExit != null) {
            System.out.print("west ");
        }
        System.out.println();
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }

        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        System.out.println("   go quit help");
    }

    /** 
     * Try to go in one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = null;
        if(direction.equals("north")) {
            nextRoom = currentRoom.northExit;
        }
        if(direction.equals("east")) {
            nextRoom = currentRoom.eastExit;
        }
        if(direction.equals("south")) {
            nextRoom = currentRoom.southExit;
        }
        if(direction.equals("west")) {
            nextRoom = currentRoom.westExit;
        }

        if (nextRoom == null) {
            System.out.println("Te das de cara contra una pared");
        }
        else {
            currentRoom = nextRoom;
            printLocationInfo();
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
