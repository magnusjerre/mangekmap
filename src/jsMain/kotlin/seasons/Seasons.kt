package seasons

import authentication.getIsAuthenticated
import csstype.Length
import csstype.px
import dto.SeasonDto
import kotlinx.coroutines.launch
import mainScope
import mui.material.CircularProgress
import mui.material.Paper
import mui.material.Size
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.material.Typography
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.router.dom.Link
import react.useEffectOnce
import react.useState
import tableBoxPadding

val Seasons = FC<Props> {
    var seasons by useState<List<SeasonDto>>(emptyList())
    var fetching by useState(false)
    var isAuthenticated by useState(false)

    useEffectOnce {
        mainScope.launch {
            fetching = true
            isAuthenticated = getIsAuthenticated()
            seasons = getSeasons(excludeEvents = true)
            fetching = false
        }
    }

    TableContainer {
        component = Paper
        sx {
            width = Length.fitContent
            padding = tableBoxPadding
        }
        Table {
            size = Size.small
            TableHead {
                TableRow {
                    TableCell { +"Navn" }
                    TableCell { +"Start år" }
                    TableCell { +"Region" }
                    if (isAuthenticated) {
                        TableCell { +"Rediger" }
                    }
                }
            }

            TableBody {
                if (fetching) {
                    CircularProgress {}
                    return@TableBody
                }

                for (season in seasons) {
                    TableRow {
                        TableCell {
                            Link {
                                to = "/seasons/${season.id}"
                                +season.name
                            }
                        }
                        TableCell { +"${season.startYear}" }
                        TableCell { +(season.region.name.substring(0, 1) + season.region.name.lowercase().substring(1))}
                        if (isAuthenticated) {
                            TableCell {
                                Link {
                                    to = "/seasons/${season.id}/edit"
                                    +"Rediger"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}