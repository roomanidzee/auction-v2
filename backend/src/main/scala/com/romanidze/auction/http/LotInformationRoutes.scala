package com.romanidze.auction.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.romanidze.auction.entities.{LotInformation, LotMarshalling}
import com.romanidze.auction.services.LotInformationService

trait LotInformationRoutes extends LotInformationService with LotMarshalling {

  import StatusCodes._
  import LotInformation._
  import LotMarshalling._

  def routes: Route = lotsRoute ~ lotRoute ~ participantsRoute

  def lotsRoute: Route =
    pathPrefix("lots") {
      pathEndOrSingleSlash {
        get {
          onSuccess(retrieveLots()) { lots =>
            complete(OK, lots)
          }
        }
      }
    }

  def lotRoute: Route =
    pathPrefix("lots" / Segment) { lot =>
      pathEndOrSingleSlash {
        post {
          entity(as[LotDescription]) { ld =>
            onSuccess(createLot(lot, ld.count)) {
              case LotCreated(newLot) => complete(Created, newLot)
              case LotExists          => complete(BadRequest, ErrorInfo(s"$lot already exists"))
            }
          }
        } ~
          get {
            onSuccess(retrieveLot(lot)) {
              _.fold(complete(NotFound))(lot => complete(OK, lot))
            }
          } ~
          delete {
            onSuccess(cancelLot(lot)) {
              _.fold(complete(NotFound))(lot => complete(OK, lot))
            }
          }
      }
    }

  def participantsRoute: Route =
    pathPrefix("lots" / Segment / "participants") { lot =>
      delete {
        pathEndOrSingleSlash {
          entity(as[ParticipantRequest]) { request =>

            onSuccess(removeParticipants(lot, request.count)) { result =>

              if (result.participants.isEmpty) {
                complete(NotFound)
              } else {
                complete(OK, result)
              }

            }

          }
        }
      }
    }

}
