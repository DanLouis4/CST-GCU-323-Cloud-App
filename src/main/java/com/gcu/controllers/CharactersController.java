package com.gcu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.gcu.business.CharacterDatabaseService;
import com.gcu.models.CharacterEntity;
import com.gcu.business.RaceDatabaseService;
import com.gcu.business.ClassDatabaseService;

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
    public String characters(Model model)
    {
        model.addAttribute("characters", characterService.getAllCharacters());
        return "characters";
    }

    @GetMapping("/characters/view/{id}")
    public String viewCharacter(@PathVariable int id, Model model)
    {
        CharacterEntity character = characterService.findById(id);
        if (character == null)
        {
            return "redirect:/characters";
        }

        model.addAttribute("character", character);
        return "view-character";
    }

    @GetMapping("/characters/create")
    public String showCreateForm(Model model)
    {
        model.addAttribute("character", new CharacterEntity());
        model.addAttribute("races", raceService.getAllRaces());
        model.addAttribute("classes", classService.getAllClasses());

        return "create-character";
    }

    @PostMapping("/characters/create")
    public String createCharacter(@ModelAttribute("character") CharacterEntity character)
    {
        characterService.addCharacter(character);
        return "redirect:/characters";
    }

    @GetMapping("/characters/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model)
    {
        CharacterEntity character = characterService.findById(id);

        if (character == null)
        {
            return "redirect:/characters";
        }

        model.addAttribute("character", character);
        model.addAttribute("races", raceService.getAllRaces());
        model.addAttribute("classes", classService.getAllClasses());

        return "edit-character";
}

    @PostMapping("/characters/edit")
    public String updateCharacter(@ModelAttribute("character") CharacterEntity character)
    {
        characterService.updateCharacter(character);
        return "redirect:/characters";
    }

    @GetMapping("/characters/delete/{id}")
    public String deleteCharacter(@PathVariable int id)
    {
        characterService.deleteCharacter(id);
        return "redirect:/characters";
    }
}