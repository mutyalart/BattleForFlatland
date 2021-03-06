package gamelogic.gamestate.serveractions
import gamelogic.gamestate.ImmutableActionCollector
import gamelogic.gamestate.gameactions.EndGame
import gamelogic.utils.IdGeneratorContainer

final class ManageEndOfGame extends ServerAction {
  def apply(currentState: ImmutableActionCollector, nowGenerator: () => Long)(
      implicit idGeneratorContainer: IdGeneratorContainer
  ): (ImmutableActionCollector, ServerAction.ServerActionOutput) = {
    val actions = Option(currentState.currentGameState.started && !currentState.currentGameState.ended)
      .filter(_ && (currentState.currentGameState.players.isEmpty || currentState.currentGameState.bosses.isEmpty))
      .map(_ => EndGame(idGeneratorContainer.gameActionIdGenerator(), nowGenerator()))
      .toList

    val (nextCollector, oldestTime, idsToRemove) = currentState.masterAddAndRemoveActions(actions)

    (nextCollector, ServerAction.ServerActionOutput(actions, oldestTime, idsToRemove))
  }
}
