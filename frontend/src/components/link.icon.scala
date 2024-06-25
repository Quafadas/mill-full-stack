package frontend

import be.doeraene.webcomponents.ui5._
import be.doeraene.webcomponents.ui5.configkeys._
import com.raquo.laminar.api.L._

def linkIcon(iconName: IconName) =
  Link(
    Icon(
      _.name := iconName,
      width := "24px",
      height := "24px"
    )
    // onClick.mapToUnit --> doSomething
  )
