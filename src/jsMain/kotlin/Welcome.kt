import authentication.getIsAuthenticated
import csstype.Display
import csstype.FontStyle
import csstype.ListStyleType
import csstype.em
import emotion.react.css
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mui.material.ListItem
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.ElementType
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.strong
import react.dom.html.ReactHTML.ul
import react.router.dom.Link
import react.useEffectOnce
import react.useState
import seasons.Seasons

external interface WelcomeProps : Props {
    var name: String?
}

external interface SubHeaderProps : Props {
    var text: String
}

private val SubHeader = FC<SubHeaderProps> { props ->
    Typography {
        variant = TypographyVariant.h4
        component = "h2".asDynamic()
        sx {
            marginTop = 1.em
        }
        +props.text
    }
}

val Welcome = FC<WelcomeProps> { props ->
    var isAuthenticated by useState(false)

    useEffectOnce {
        mainScope.launch {
            isAuthenticated = getIsAuthenticated()
        }
    }

    Typography {
        variant = TypographyVariant.h2
        component = "h1".asDynamic()
        +"Mangekampen"
    }

    if (isAuthenticated) {
        Link {
            to = "/persons"
            +"Deltakere"
        }
    }

    SubHeader {
        text = "Sesonger"
    }

    Seasons {}

    SubHeader {
        text = "Informasjon"
    }

    Typography {
        variant = TypographyVariant.body1
        strong { +"Mangekampen " }
        +"er den desidert mest populære aktiviteten i bedriftsidrettslaget (du kan bli med på så mange øvelser du vil). Mangekampen består av 15 individuelle øvelser som går fra august til juni. Øvelsene er delt inn i tre kategorier:"
    }

    Typography {
        ul {
            li { +"Kondisjon" }
            li { +"Teknikk" }
            li { +"Ball" }
        }
    }

    Typography {
        +"Hvis du har vært med på 8 forskjellige øvelser og har fått med deg minst én øvelse i hver kategori, er du kvalifisert som Mangekjemper og får være med på avslutningsturen. Den har tidligere gått til både Norge, København, Gøteborg, Stockholm, Berlin, London, og Warzawa. Hvor den går neste gang er en hemmelighet."
    }

    Typography {
        +"Mangekampen er en fantastisk sosial og hyggelig aktivitet å være med på. Selv om det alltid er noen som er mer seriøse enn andre, er flertallet hovedsakelig med for å treffe andre kolleger – i litt sportslige omgivelser. Mangekampøvelsene varierer fra år til år, men kan være f.eks. minigolf, tennis, terrengløp, skyting, badminton, dart, langrenn, skøyter, orientering, squash, friidrett, sykling, bowling, poker, fekting, gokart, segway, roing, svømming, frisbeegolf, fotballgolf og som regel en overraskelsesøvelse på avslutningsturen."
    }

    SubHeader {
        text = "Mangekampens reglement"
    }

    Typography {
        +"Mangekampens formål er å få flest mulig til å fullføre minst 8 øvelser for derved å få tittelen Mangekjemper. Det samme kriteriet gjelder for å være kvalifisert til avslutningsturen. Det skal selvfølgelig også nevnes aspektet med å prøve å være i noenlunde brukbar fysisk form."
    }

    Typography {
        +"Mangekampens består av i alt 15 øvelser som igjen er delt inn i tre hovedgrupper:"
    }

    Typography {
        ul {
            li {
                strong { +"Ball: " }
                +"Eksempelvis tennis, badminton, squash, bordtennis, fotballgolf og minigolf (av og til er dette en teknikkøvelse...)"
            }
            li {
                strong { +"Teknikk: " }
                +"Eksempelvis dart, bowling, friidrett, skøyter, skyting, frisbeegolf, fekting og friidrett"
            }
            li {
                strong { +"Kondisjon: " }
                +"Eksempelvis langrenn, terrengløp, roing, orientering og sykling I tillegg vil det på avslutningsturen bli innlagt en eller to ekstra øvelser som holdes hemmelig frem til turen."
            }
        }
    }

    Typography {
        +"For å kunne bli titulert som mangekjemper må en i tillegg til å gjennomføre minst 8 øvelser, ha gjennomført minst en øvelse i hver hovedgruppe. Dvs at du ikke kan utelate balløvelsene selv om du har deltatt i 8 øvelser i de to andre kategoriene til sammen."
    }

    Typography {
        +"For å belønne allsidighet er følgende regel lagt inn fra sesongen 2008-2009: Det er kun de 3 beste resultatene fra gruppene ball og kondisjon som teller med i summen for poeng etter 8 øvelser. Hvis man må la den 4. øvelsen telle vil den gi 16 poeng for guttene og 8 for jentene. Rekkefølgen for resterende utøvere i øvelsen endres ikke selv om en utøver får 16 poeng i stedet for eksempelvis 1 poeng i en øvelse. (Eks: Ola Nordmann vinner alle balløvelsene og får tellende poengsum for balløvelsene (1,1,1,16), men han som får 2. plass i øvelsen Ola Nordmann får 16 poeng for, får fremdeles 2 poeng for denne øvelsen)."
    }

    Typography {
        span {
            css { fontStyle = FontStyle.italic }
            +"Strykninger: "
        }
        +"Alle som ikke gjennomfører minst 8 øvelser som anført ovenfor blir strøket av sammenlagtlisten etter endt sesong. Alle med minst 8 øvelser og definert som mangekjemper rykker da opp på listene og ny liste settes opp. For alle med mer enn 8 øvelser strykes de dårligste øvelsene (med de begrensninger som er nevnt ovenfor) og de 8 beste øvelsene teller på sammenlagtlisten. Ved poenglikhet på sammenlagtlisten vinner den som har flest førsteplasser osv. Alle som får tittelen mangekjemper blir premiert. Vandrepremie/mangekamppokal må vinnes fem ganger før den vinnes til odel og eie. For å få tellende poeng i øvelsen må deltaker stille til start. Man får også deltakelse ved å bidra på andre måter (være tidtaker, være fotograf eller bare dukke opp og være sosial), men da får man sisteplass."
    }

    Typography {
        +"Følgende regler gjelder for balløvelsene og øvelser hvor gruppespill blir nødvendig:"
    }

    Typography {
        ul {
            li { +"Pliktig påmelding før arrangementet." }
            li { +"I utgangspunktet spiller alle mot alle." }
            li { +"""Der hvor antallet påmeldte er så stort at det av praktiske årsaker ikke lar seg gjennomføre, deles det opp i flere puljer hvor de aller beste fra hver pulje møtes i et "sluttspill" eller "finale". Arrangøren bestemmer og informerer om de innledende resultatene skal telle. De gjør de eksempelvis på innendørs roing, men ikke i Dart-finalen.""" }
            li { +"Ved likt antall seire teller innbyrdes oppgjør." }
            li { +"Dersom flere enn to har likt antall seire telles målforskjell i innbyrdes oppgjør." }
            li { +"For de som ikke kommer til en eventuell finale teller antall seire i innledende kamper. Ved like antall seire går den som deltok i vinnerens pulje foran." }
        }
    }

    Typography {
        +"Poengsystemet er som følger:"
    }

    Typography {
        ol {
            li { +"Plass --> 1 poeng" }
            li { +"Plass --> 2 poeng" }
            li { +"Plass --> 3 poeng" }
            li { +"Osv." }
        }
    }
}