package ca.cmpt213.a4.onlinehangman.controllers;
import ca.cmpt213.a4.onlinehangman.model.Game;
import ca.cmpt213.a4.onlinehangman.model.Message;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class for controller holding all things to run
 * @YIXU YE
 * @SFU ID： 301368702
 * @SFU EMAIL: yixuy@sfu.ca
 */

@Controller
public class HangmanController {
    //a resusable String object to display a prompt message at the screen
    private Message promptMessage;
    //works like a constructor, but wait until dependency injection is done, so it's more like a setup
    @PostConstruct
    public void hangmanControllerInit() {
        promptMessage = new Message("Initializing...");
    }
    @GetMapping("/helloworld")
    public String showHelloWorldPage(Model model) throws GameNotFoundException {
        promptMessage.setMessage("You are at the helloworld page!");
        model.addAttribute("promptMessage", promptMessage);
        // take the user to helloworld.html
        return "helloworld";
    }
    // Atomic variable for ID
    private AtomicLong nextId = new AtomicLong();
    // Game list for storing game information
    private List<Game> games = new ArrayList<>();

    // Get the welcome page
    @GetMapping("/welcome")
    public String showWelcomePage(Model model) {
            return "welcome";
    }
    // Get the game information page
    @GetMapping("/game")
    public String getGameInfo(Model model, HttpSession httpSession){
        // Word base read could be faster as static
        List<String> wordBase =  extractTxt();
        Random random = new Random();
        String targetWord = wordBase.get(random.nextInt(wordBase.size()));
        String shownWord = "";
        for (int i = 0; i < targetWord.length(); i++) {
            shownWord += "_ ";
        }

        Game game = new Game();
        game.setId(nextId.incrementAndGet());
        game.setTargetWord(targetWord);
        game.setShowWord(shownWord);
        games.add(game);

        httpSession.setAttribute("newGame", game);
        model.addAttribute("game", game);
        return "game";
    }
    // Deal with the input char and update game information page
    // Get page guess character
    // Update the httpSession and game status
    @PostMapping("/game")
    public String gamePage(@ModelAttribute("game") Game oldGame, HttpSession httpSession, Model model){
        // oldGame for receive the user input
        String inputChar = oldGame.getGuessChar();
        Game game = (Game) httpSession.getAttribute("newGame");
        if(!inputChar.equals(" ")){
            game.updateGame(inputChar);
        }
        httpSession.setAttribute("newGame", game);
        model.addAttribute("game", game);
        if(game.getStatus().equals("Active")){
            // partial update the game
            return "game::update";
        }else {
            // Model for gameOver
            return "gameover";
        }
    }
    // Extract the txt by absolute path to get the word base
    private List<String> extractTxt() {
        BufferedReader reader;
        List<String> words = new ArrayList<>();
        try{
            reader = new BufferedReader(new FileReader(Paths.get("commonWords.txt").toAbsolutePath().toString()));
            String line = reader.readLine();
            while (line != null) {
                words.add(line.trim());
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    // Search for game by ID and display the model
    @GetMapping("/game/{id}")
    public String getIdGame(@PathVariable("id") long gameId,Model model) throws GameNotFoundException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes)requestAttributes).getResponse();
        if(gameId > games.size()){
            throw new GameNotFoundException("not Found");
        }
        Game game = games.get((int)(gameId-1));
        model.addAttribute("game", game);
        if(game.getStatus().equals("Active")){
            // partial update the game
            return "game";
        }else {
            return "gameover";
        }
    }
    // Handle the exception when page is not found
    //https://stackoverflow.com/questions/36848562/add-a-body-to-a-404-not-found-exception
    @ExceptionHandler(GameNotFoundException.class)
    public ModelAndView gameNotFound(HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView();
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        modelAndView.setViewName("gamenotfound");
        return modelAndView;
    }
}// HangmanController.java