package frontend

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.*

def linkIcon(iconName: IconName) =
  Link(
    Icon(
      _.name := iconName,
      width := "24px",
      height := "24px"
    )
    // onClick.mapToUnit --> doSomething
  )
