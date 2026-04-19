package com.gcu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.gcu.business.CharacterDatabaseService;
import com.gcu.models.CharacterEntity;
import com.gcu.business.RaceDatabaseService;
import com.gcu.business.ClassDatabaseService;
import com.gcu.models.UserEntity;

// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CharactersController
{
    private static final Logger logger = LoggerFactory.getLogger(CharactersController.class);

    private final CharacterDatabaseService characterService;
    private final RaceDatabaseService raceService;
    private final ClassDatabaseService classService;
    
    public CharactersController(CharacterDatabaseService characterService,
                                RaceDatabaseService raceService,
                                ClassDatabaseService classService)
    {
        this.characterService = characterService;
        this.raceService = raceService;
        this.classService = classService;
    }


    @GetMapping("/characters")
    public String characters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "ownerPriority", required = false) String ownerPriority,
            Model model,
            HttpSession session)
    {
        logger.info("Entering characters() with keyword={}, sortBy={}, ownerPriority={}", keyword, sortBy, ownerPriority);

        UserEntity user = (UserEntity) session.getAttribute("user");

        model.addAttribute("characters",
                characterService.searchAndSortVisibleCharacters(user, keyword, sortBy, ownerPriority));

        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ownerPriority", ownerPriority);
        model.addAttribute("session", session);

        logger.info("Exiting characters() -> characters");
        return "characters";
    }

    @GetMapping("/characters/view/{id}")
    public String viewCharacter(@PathVariable int id, Model model, HttpSession session)
    {
        logger.info("Entering viewCharacter() with id={}", id);

        CharacterEntity character = characterService.findById(id);
        if (character == null)
        {
            logger.warn("Character with id={} not found", id);
            logger.info("Exiting viewCharacter() -> redirect:/characters");
            return "redirect:/characters";
        }

        model.addAttribute("character", character);
        model.addAttribute("session", session);
        logger.info("Exiting viewCharacter() -> view-character");
        return "view-character";
    }

    @GetMapping("/characters/create")
    public String showCreateForm(Model model, HttpSession session)
    {

        logger.info("Entering showCreateForm()");

        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null)
        {
            logger.warn("Unauthorized access attempt to showCreateForm()");
            logger.info("Exiting showCreateForm() -> redirect:/signin");
            return "redirect:/signin";
        }

        model.addAttribute("character", new CharacterEntity());
        model.addAttribute("races", raceService.getAllRaces());
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("session", session);

        logger.info("Exiting showCreateForm() -> create-character");
        return "create-character";
    }

    @PostMapping("/characters/create")
    public String createCharacter(@ModelAttribute("character") CharacterEntity character, HttpSession session)
    {
        logger.info("Entering createCharacter() with character={}", character.getCharacterName());

        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null)
        {
            logger.warn("Unauthorized access attempt to createCharacter()");
            logger.info("Exiting createCharacter() -> redirect:/signin");
            return "redirect:/signin";
        }

        characterService.addCharacter(character, user);

        logger.info("Character added with id={} for user={}", character.getCharacterId(), user.getUsername());
        logger.info("Exiting createCharacter() -> redirect:/characters");
        return "redirect:/characters";
    }
    
    @GetMapping("/characters/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, HttpSession session)
    {
        logger.info("Entering showEditForm() with id={}", id);

        UserEntity user = (UserEntity) session.getAttribute("user");
        CharacterEntity character = characterService.findById(id);

        if (user == null || character == null || character.getUser() == null ||
            !character.getUser().getUserId().equals(user.getUserId()))
        {
            logger.warn("Unauthorized access attempt to showEditForm() with id={}", id);
            logger.info("Exiting showEditForm() -> redirect:/characters");
            return "redirect:/characters";
        }

        model.addAttribute("character", character);
        model.addAttribute("races", raceService.getAllRaces());
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("session", session);

        logger.info("Exiting showEditForm() -> edit-character");
        return "edit-character";
    }

    @PostMapping("/characters/edit")
    public String updateCharacter(@ModelAttribute("character") CharacterEntity character, HttpSession session)
    {
        logger.info("Entering updateCharacter() with character={}", character.getCharacterId());

        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null)
        {
            logger.warn("Unauthorized access attempt to updateCharacter()");
            logger.info("Exiting updateCharacter() -> redirect:/signin");
            return "redirect:/signin";
        }

        character.setUser(user);
        characterService.updateCharacter(character);

        logger.info("Successful update for character id={} for user={}", character.getCharacterId(), user.getUsername());
        logger.info("Exiting updateCharacter() -> redirect:/characters");
        return "redirect:/characters";
    }

    @GetMapping("/characters/delete/{id}")
    public String deleteCharacter(@PathVariable int id, HttpSession session)
    {
        logger.info("Entering deleteCharacter() with id={}", id);

        UserEntity user = (UserEntity) session.getAttribute("user");

        CharacterEntity character = characterService.findById(id);

        if (user == null || character == null || character.getUser() == null ||
            !character.getUser().getUserId().equals(user.getUserId()))
        {
            logger.warn("Unauthorized access attempt to deleteCharacter() with id={}", id);
            logger.info("Exiting deleteCharacter() -> redirect:/characters");
            return "redirect:/characters";
        }

        characterService.deleteCharacter(id);

        logger.info("Character deleted successfully: id={} by user={}", id, user.getUsername());
        logger.info("Exiting deleteCharacter() -> redirect:/characters");
        return "redirect:/characters";
    }
}