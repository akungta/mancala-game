package com.akashrungta.game.mancala.api;

import com.akashrungta.game.mancala.core.Board;
import com.akashrungta.game.mancala.core.BoardView;
import com.akashrungta.game.mancala.core.Player;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.PATCH;
import io.swagger.annotations.Api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Api
@Path("/mancala/game")
@Produces(MediaType.APPLICATION_JSON)
public class GameApi {

  private ConcurrentMap<String, Board> games = new ConcurrentHashMap<>();

  @POST
  @Timed
  @Path("/start")
  public BoardView start(@QueryParam("start_player") @DefaultValue("1") @Valid Player startPlayer) {
    String sessionId = UUID.randomUUID().toString();
    Board board = new Board(startPlayer, sessionId);
    games.put(sessionId, board);
    return board.getView();
  }

  @GET
  @Timed
  @Path("/{session_id}/get")
  public BoardView get(@PathParam("session_id") String sessionId) {
    return games
        .compute(
            sessionId,
            (seId, board) -> {
              if (board == null) {
                throw new WebApplicationException("No such session", Response.Status.BAD_REQUEST);
              }
              return board;
            })
        .getView();
  }

  @PATCH
  @Timed
  @Path("/{session_id}/play")
  public BoardView play(
      @PathParam("session_id") String sessionId,
      @QueryParam("player") @NotNull Player player,
      @QueryParam("pit_position") @NotNull int pitPosition) {
    return games
        .compute(
            sessionId,
            (seId, board) -> {
              if (board == null) {
                throw new WebApplicationException("No such session", Response.Status.BAD_REQUEST);
              }
              if (board.getNextPlayer() != player) {
                throw new WebApplicationException(
                    "Wrong Player's turn", Response.Status.BAD_REQUEST);
              }
              if (pitPosition < 0 || pitPosition >= board.PITS_SIZE) {
                throw new WebApplicationException("Invalid position", Response.Status.BAD_REQUEST);
              }
              try {
                board.play(player, pitPosition);

                if (board.isGameFinished()) {
                  games.remove(seId);
                }

              } catch (RuntimeException e) {
                throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
              }
              return board;
            })
        .getView();
  }
}
