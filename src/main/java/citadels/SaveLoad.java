package citadels;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;
import java.util.*;

public class SaveLoad {
    private Game game;
    private List<Player> players;
    private Deck districtDeck;
    private List<CharacterCard> characterDeck;

    public SaveLoad(Game game, List<Player> players, Deck districtDeck, List<CharacterCard> characterDeck) {
        this.game = game;
        this.players = players;
        this.districtDeck = districtDeck;
        this.characterDeck = characterDeck;
    }

    public void saveGame(String filename) {
        JSONObject obj = createGameState();
        writeToFile(filename, obj);
    }

    private JSONObject createGameState() {
        JSONObject obj = new JSONObject();
        obj.put("round", game.getRound());
        obj.put("crownedPlayerIndex", game.getCrownedPlayerIndex());
        obj.put("players", createPlayersArray());
        obj.put("deck", createDeckArray());
        return obj;
    }

    private JSONArray createPlayersArray() {
        JSONArray playersArr = new JSONArray();
        for (Player p : players) {
            JSONObject pObj = createPlayerObject(p);
            playersArr.add(pObj);
        }
        return playersArr;
    }

    private JSONObject createPlayerObject(Player p) {
        JSONObject pObj = new JSONObject();
        pObj.put("id", p.getId());
        pObj.put("isHuman", p.isHuman());
        pObj.put("gold", p.getGold());
        pObj.put("hasCrown", p.hasCrown());
        pObj.put("hand", createCardsArray(p.getHand()));
        pObj.put("city", createCardsArray(p.getCity()));

        if (p.getCharacter() != null) {
            pObj.put("character", p.getCharacter().getName());
        }
        return pObj;
    }

    private JSONArray createCardsArray(List<DistrictCard> cards) {
        JSONArray cardArr = new JSONArray();
        for (DistrictCard c : cards) {
            cardArr.add(createCardObject(c));
        }
        return cardArr;
    }

    private JSONObject createCardObject(DistrictCard c) {
        JSONObject cObj = new JSONObject();
        cObj.put("name", c.name);
        cObj.put("color", c.color);
        cObj.put("cost", c.cost);
        cObj.put("text", c.text);
        return cObj;
    }

    private void writeToFile(String filename, JSONObject obj) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(obj.toJSONString());
            System.out.println("Game saved to " + filename);
        } catch (Exception e) {
            System.out.println("Failed to save game: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {
        try {
            JSONObject obj = readFromFile(filename);
            restoreGameState(obj);
            System.out.println("Game loaded from " + filename);
        } catch (Exception e) {
            System.out.println("Failed to load game: " + e.getMessage());
        }
    }

    private JSONObject readFromFile(String filename) throws Exception {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filename)) {
            return (JSONObject) parser.parse(reader);
        }
    }

    private void restoreGameState(JSONObject obj) {
        // Restore basic game state
        game.setRound(((Long) obj.get("round")).intValue());
        game.setCrownedPlayerIndex(((Long) obj.get("crownedPlayerIndex")).intValue());

        // Restore players
        JSONArray playersArr = (JSONArray) obj.get("players");
        for (int i = 0; i < playersArr.size(); i++) {
            JSONObject pObj = (JSONObject) playersArr.get(i);
            restorePlayer(players.get(i), pObj);
        }

        // Restore deck
        JSONArray deckArr = (JSONArray) obj.get("deck");
        restoreDeck(deckArr);
    }

    private void restorePlayer(Player p, JSONObject pObj) {
        // Clear existing state
        p.getHand().clear();
        p.getCity().clear();

        // Restore basic properties
        p.setCrown(((Boolean) pObj.get("hasCrown")).booleanValue());
        p.addGold(((Long) pObj.get("gold")).intValue());

        // Restore hand
        JSONArray handArr = (JSONArray) pObj.get("hand");
        for (Object cardObj : handArr) {
            p.addToHand(createDistrictCard((JSONObject) cardObj));
        }

        // Restore city
        JSONArray cityArr = (JSONArray) pObj.get("city");
        for (Object cardObj : cityArr) {
            p.buildDistrict(createDistrictCard((JSONObject) cardObj));
        }

        // Restore character if present
        if (pObj.containsKey("character")) {
            String charName = (String) pObj.get("character");
            for (CharacterCard cc : characterDeck) {
                if (cc.getName().equals(charName)) {
                    p.setCharacter(cc);
                    break;
                }
            }
        }
    }

    private DistrictCard createDistrictCard(JSONObject cardObj) {
        return new DistrictCard(
                (String) cardObj.get("name"),
                (String) cardObj.get("color"),
                ((Long) cardObj.get("cost")).intValue(),
                (String) cardObj.get("text"));
    }

    private void restoreDeck(JSONArray deckArr) {
        // Clear existing deck
        while (districtDeck.size() > 0) {
            districtDeck.draw();
        }

        // Restore cards to deck
        for (Object cardObj : deckArr) {
            districtDeck.addCard(createDistrictCard((JSONObject) cardObj));
        }
    }

    private JSONArray createDeckArray() {
        JSONArray deckArr = new JSONArray();
        for (DistrictCard card : districtDeck.getCards()) {
            deckArr.add(createCardObject(card));
        }
        return deckArr;
    }
}