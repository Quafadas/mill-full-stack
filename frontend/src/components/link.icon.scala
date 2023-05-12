package frontend

import io.laminext.syntax.core._
import com.raquo.laminar.api.L.{*, given}
import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*


def linkIcon(iconName: IconName, doSomething: Observer[Unit] = Observer[Unit] { Unit => () }) =
  Link(
    Icon(
      _.name := iconName,
      width := "24px",
      height := "24px"
    ),
    onClick.mapToUnit --> doSomething
  )
