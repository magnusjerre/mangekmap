class ApiEvents {
    companion object {
        const val BASE_PATH = "/api/events"
        const val PARTICIPATIONS_PERSONID = "$BASE_PATH/participations/{personId}"
        const val ID = "$BASE_PATH/{id}"
        const val ID_PARTICIPANTS = "$BASE_PATH/{eventId}/participants"
    }
}

class ApiSeasons {
    companion object {
        const val BASE_PATH = "/api/seasons"
        const val ID = "$BASE_PATH/{id}"
        const val ID_EVENTS = "$BASE_PATH/{id}/events"
    }
}

class ApiPersons {
    companion object {
        const val BASE_PATH = "/api/persons"
        const val ID = "$BASE_PATH/{id}"
    }
}

class ApiCategories {
    companion object {
        const val BASE_PATH = "/api/categories"
        const val ID = "$BASE_PATH/{id}"
    }
}

class ApiAdmin {
    companion object {
        const val BASE_PATH = "/api/admin"
    }
}

class ApiAuthentication {
    companion object {
        const val BASE_PATH = "/check_authentication"
    }
}

class ApiCsvImport {
    companion object {
        const val BASE_PATH = "/api/import"
        const val PERSONS = "$BASE_PATH/persons"
        const val EVENTS = "$BASE_PATH/events"
    }
}
