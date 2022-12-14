import authentication.getIsAuthenticated
import csstype.FontStyle
import csstype.em
import emotion.react.css
import kotlinx.coroutines.launch
import mui.material.Stack
import mui.material.StackDirection
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
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

    Stack {
        direction = responsive(StackDirection.row)
        spacing = responsive(2)
        sx {
            marginTop = 1.em
        }

        if (isAuthenticated) {
            a {
                href = "/logout"
                +"Log ut"
            }
            Link {
                to = "/persons"
                +"Deltakere"
            }
            Link {
                to = "/seasons/new"
                +"Ny sesong"
            }
        } else {
            Link {
                to = "/custom/login"
                +"Til login"
            }
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
        +"er den desidert mest popul??re aktiviteten i bedriftsidrettslaget (du kan bli med p?? s?? mange ??velser du vil). Mangekampen best??r av 15 individuelle ??velser som g??r fra august til juni. ??velsene er delt inn i tre kategorier:"
    }

    Typography {
        ul {
            li { +"Kondisjon" }
            li { +"Teknikk" }
            li { +"Ball" }
        }
    }

    Typography {
        +"Hvis du har v??rt med p?? 8 forskjellige ??velser og har f??tt med deg minst ??n ??velse i hver kategori, er du kvalifisert som Mangekjemper og f??r v??re med p?? avslutningsturen. Den har tidligere g??tt til b??de Norge, K??benhavn, G??teborg, Stockholm, Berlin, London, og Warzawa. Hvor den g??r neste gang er en hemmelighet."
    }

    Typography {
        +"Mangekampen er en fantastisk sosial og hyggelig aktivitet ?? v??re med p??. Selv om det alltid er noen som er mer seri??se enn andre, er flertallet hovedsakelig med for ?? treffe andre kolleger ??? i litt sportslige omgivelser. Mangekamp??velsene varierer fra ??r til ??r, men kan v??re f.eks. minigolf, tennis, terrengl??p, skyting, badminton, dart, langrenn, sk??yter, orientering, squash, friidrett, sykling, bowling, poker, fekting, gokart, segway, roing, sv??mming, frisbeegolf, fotballgolf og som regel en overraskelses??velse p?? avslutningsturen."
    }

    SubHeader {
        text = "Mangekampens reglement"
    }

    Typography {
        +"Mangekampens form??l er ?? f?? flest mulig til ?? fullf??re minst 8 ??velser for derved ?? f?? tittelen Mangekjemper. Det samme kriteriet gjelder for ?? v??re kvalifisert til avslutningsturen. Det skal selvf??lgelig ogs?? nevnes aspektet med ?? pr??ve ?? v??re i noenlunde brukbar fysisk form."
    }

    Typography {
        +"Mangekampens best??r av i alt 15 ??velser som igjen er delt inn i tre hovedgrupper:"
    }

    Typography {
        ul {
            li {
                strong { +"Ball: " }
                +"Eksempelvis tennis, badminton, squash, bordtennis, fotballgolf og minigolf (av og til er dette en teknikk??velse...)"
            }
            li {
                strong { +"Teknikk: " }
                +"Eksempelvis dart, bowling, friidrett, sk??yter, skyting, frisbeegolf, fekting og friidrett"
            }
            li {
                strong { +"Kondisjon: " }
                +"Eksempelvis langrenn, terrengl??p, roing, orientering og sykling I tillegg vil det p?? avslutningsturen bli innlagt en eller to ekstra ??velser som holdes hemmelig frem til turen."
            }
        }
    }

    Typography {
        +"For ?? kunne bli titulert som mangekjemper m?? en i tillegg til ?? gjennomf??re minst 8 ??velser, ha gjennomf??rt minst en ??velse i hver hovedgruppe. Dvs at du ikke kan utelate ball??velsene selv om du har deltatt i 8 ??velser i de to andre kategoriene til sammen."
    }

    Typography {
        +"For ?? bel??nne allsidighet er f??lgende regel lagt inn fra sesongen 2008-2009: Det er kun de 3 beste resultatene fra gruppene ball og kondisjon som teller med i summen for poeng etter 8 ??velser. Hvis man m?? la den 4. ??velsen telle vil den gi 16 poeng for guttene og 8 for jentene. Rekkef??lgen for resterende ut??vere i ??velsen endres ikke selv om en ut??ver f??r 16 poeng i stedet for eksempelvis 1 poeng i en ??velse. (Eks: Ola Nordmann vinner alle ball??velsene og f??r tellende poengsum for ball??velsene (1,1,1,16), men han som f??r 2. plass i ??velsen Ola Nordmann f??r 16 poeng for, f??r fremdeles 2 poeng for denne ??velsen)."
    }

    Typography {
        span {
            css { fontStyle = FontStyle.italic }
            +"Strykninger: "
        }
        +"Alle som ikke gjennomf??rer minst 8 ??velser som anf??rt ovenfor blir str??ket av sammenlagtlisten etter endt sesong. Alle med minst 8 ??velser og definert som mangekjemper rykker da opp p?? listene og ny liste settes opp. For alle med mer enn 8 ??velser strykes de d??rligste ??velsene (med de begrensninger som er nevnt ovenfor) og de 8 beste ??velsene teller p?? sammenlagtlisten. Ved poenglikhet p?? sammenlagtlisten vinner den som har flest f??rsteplasser osv. Alle som f??r tittelen mangekjemper blir premiert. Vandrepremie/mangekamppokal m?? vinnes fem ganger f??r den vinnes til odel og eie. For ?? f?? tellende poeng i ??velsen m?? deltaker stille til start. Man f??r ogs?? deltakelse ved ?? bidra p?? andre m??ter (v??re tidtaker, v??re fotograf eller bare dukke opp og v??re sosial), men da f??r man sisteplass."
    }

    Typography {
        +"F??lgende regler gjelder for ball??velsene og ??velser hvor gruppespill blir n??dvendig:"
    }

    Typography {
        ul {
            li { +"Pliktig p??melding f??r arrangementet." }
            li { +"I utgangspunktet spiller alle mot alle." }
            li { +"""Der hvor antallet p??meldte er s?? stort at det av praktiske ??rsaker ikke lar seg gjennomf??re, deles det opp i flere puljer hvor de aller beste fra hver pulje m??tes i et "sluttspill" eller "finale". Arrang??ren bestemmer og informerer om de innledende resultatene skal telle. De gj??r de eksempelvis p?? innend??rs roing, men ikke i Dart-finalen.""" }
            li { +"Ved likt antall seire teller innbyrdes oppgj??r." }
            li { +"Dersom flere enn to har likt antall seire telles m??lforskjell i innbyrdes oppgj??r." }
            li { +"For de som ikke kommer til en eventuell finale teller antall seire i innledende kamper. Ved like antall seire g??r den som deltok i vinnerens pulje foran." }
        }
    }

    Typography {
        +"Poengsystemet er som f??lger:"
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