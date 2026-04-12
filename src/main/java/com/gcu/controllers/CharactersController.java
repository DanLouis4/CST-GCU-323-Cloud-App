package com.gcu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.gcu.business.CharacterDatabaseService;
import com.gcu.models.CharacterEntity;
import com.gcu.business.RaceDatabaseService;
import com.gcu.business.ClassDatabaseService;
import com.gcu.models.UserEntity;

@Controller
public class CharactersController
{
    @Autowired
    private CharacterDatabaseService characterService;

    @Autowired
    private RaceDatabaseService raceService;

    @Autowired
    private ClassDatabaseService classService;

    @GetMapping("/characters")
    public String characters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "ownerPriority", required = false) String ownerPriority,
            Model model,
            HttpSession session)
    {
        UserEntity user = (UserEntity) session.getAttribute("user");

        model.addAttribute("characters",
                characterService.searchAndSortVisibleCharacters(user, keyword, sortBy, ownerPriority));

        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ownerPriority", ownerPriority);
        model.addAttribute("session", session);

        return "characters";
    }

    @GetMapping("/characters/view/{id}")
    public String viewCharacter(@PathVariable int id, Model model, HttpSession session)
    {
        CharacterEntity character = characterService.findById(id);
        if (character == null)
        {
            return "redirect:/characters";
        }

        model.addAttribute("character", character);
        model.addAttribute("session", session);
        return "view-character";
    }

    @GetMapping("/characters/create")
    public String showCreateForm(Model model, HttpSession session)
    {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null)
        {
            return "redirect:/signin";
        }

        model.addAttribute("character", new CharacterEntity());
        model.addAttribute("races", raceService.getAllRaces());
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("session", session);
        return "create-character";
    }

    @PostMapping("/characters/create")
    public String createCharacter(@ModelAttribute("character") CharacterEntity character, HttpSession session)
    {
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null)
        {
            return "redirect:/signin";
        }

        characterService.addCharacter(character, user);
        return "redirect:/characters";
    }
    
    @GetMapping("/characters/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, HttpSession session)
    {
        UserEntity user = (UserEntity) session.getAttribute("user");
        CharacterEntity character = characterService.findById(id);

        if (user == null || character == null || character.getUser() == null ||
            !character.getUser().getUserId().equals(user.getUserId()))
        {
            return "redirect:/characters";
        }

        model.addAttribute("character", character);
        model.addAttribute("races", raceService.getAllRaces());
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("session", session);

        return "edit-character";
    }

    @PostMapping("/characters/edit")
    public String updateCharacter(@ModelAttribute("character") CharacterEntity character, HttpSession session)
    {
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null)
        {
            return "redirect:/signin";
        }

        character.setUser(user);
        characterService.updateCharacter(character);
        return "redirect:/characters";
    }

    @GetMapping("/characters/delete/{id}")
    public String deleteCharacter(@PathVariable int id, HttpSession session)
    {
        UserEntity user = (UserEntity) session.getAttribute("user");
        CharacterEntity character = characterService.findById(id);

        if (user == null || character == null || character.getUser() == null ||
            !character.getUser().getUserId().equals(user.getUserId()))
        {
            return "redirect:/characters";
        }

        characterService.deleteCharacter(id);
        return "redirect:/characters";
    }
}