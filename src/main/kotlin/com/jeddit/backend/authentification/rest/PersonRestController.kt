package com.jeddit.backend.authentification.rest

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import java.util.ArrayList


data class Person(val name: String, val email: String)


@RestController
object PersonRestController {
    private val persons: MutableList<Person>

    init {
        persons = ArrayList()
        persons.add(Person("Hello", "World"))
        persons.add(Person("Foo", "Bar"))
    }

    @RequestMapping(path = arrayOf("/persons"), method = arrayOf(RequestMethod.GET))
    fun getPersons(): List<Person> {
        return persons
    }

    @RequestMapping(path = arrayOf("/persons/{name}"), method = arrayOf(RequestMethod.GET))
    fun getPerson(@PathVariable("name") name: String): Person? {
        return persons.stream()
                .filter { person -> name.equals(person.name!!, ignoreCase = true) }
                .findAny().orElse(null)
    }
}
