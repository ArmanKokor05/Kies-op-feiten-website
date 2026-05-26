package com.sop.backend.controllers;

import com.sop.backend.models.Election;
import com.sop.backend.services.XmlService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/xml")
@CrossOrigin(origins = "*")
public class XmlController {

    private final XmlService xmlService;

    public XmlController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping
    public List<Election> getAllElections() {
        return xmlService.getAllElections();
    }

    @GetMapping("/{id}")
    public Election getElectionById(@PathVariable Long id) {
        return xmlService.getElectionById(id);
    }

    @PostMapping
    public Election uploadXml(@RequestParam("file") MultipartFile file) {
        return xmlService.processXmlFile(file);
    }

    @PostMapping("/batch")
    public List<Election> uploadMultipleXml(@RequestParam("files") MultipartFile[] files) {
        return xmlService.processBatchXmlFiles(files);
    }
}