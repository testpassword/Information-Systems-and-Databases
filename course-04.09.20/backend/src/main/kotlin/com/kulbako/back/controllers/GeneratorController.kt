package com.kulbako.back.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController @RequestMapping(path = ["/generator"])
class GeneratorController {

    @PutMapping private fun generateRecords(): ResponseEntity<String> {
        //TODO: сгенерировать
        return ResponseEntity("ОТЧЁТ", HttpStatus.OK)
    }

    @DeleteMapping private fun clearRecords(): ResponseEntity<String> {
        //TODO: удалить всё
        return ResponseEntity("ОТЧЁТ", HttpStatus.OK)
    }
}