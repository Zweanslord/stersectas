package stersectas.view.game;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import stersectas.application.game.DetailedGame;
import stersectas.application.game.GameQueryService;

@Controller
@RequestMapping("/game")
public class ViewGameController {

	private final GameQueryService gameQueryService;

	@Autowired
	public ViewGameController(GameQueryService gameQueryService) {
		this.gameQueryService = gameQueryService;
	}

	@RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
	public String view(@PathVariable String gameId, HttpServletRequest request, Model model)
			throws NoSuchRequestHandlingMethodException {
		DetailedGame game = gameQueryService.findDetailedGameById(gameId)
				.orElseThrow(() -> new NoSuchRequestHandlingMethodException(request));
		model.addAttribute("game", game);
		return "member/game/view";
	}
}