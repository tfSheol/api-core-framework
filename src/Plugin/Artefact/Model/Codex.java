package Plugin.Artefact.Model;

import Core.Database.SQLite;
import Obj.ArtefactObj;
import Tool.SQLGetRequest;

import java.util.ArrayList;

/**
 * Created by teddy on 07/05/2016.
 */
public class Codex {
    private ArrayList<ArtefactObj> artefactObj = new ArrayList<>();

    public Codex() {
        SQLite sql = new SQLite(SQLGetRequest.ARTEFACTS);
        sql.select();
        for (int i = 0; i < sql.getResultSet().size(); i++) {
            ArtefactObj artefactObjTmp = new ArtefactObj();
            artefactObjTmp.id = (int) sql.getResultSet().get(i).get("artefacts.id");
            artefactObjTmp.quality_id = (int) sql.getResultSet().get(i).get("artefacts.quality_id");
            artefactObjTmp.collection_id = (int) sql.getResultSet().get(i).get("artefacts.collection_id");
            artefactObjTmp.type_id = (int) sql.getResultSet().get(i).get("artefacts.type_id");
            artefactObjTmp.name = (String) sql.getResultSet().get(i).get("artefacts.name");
            artefactObjTmp.drop_rate = (int) sql.getResultSet().get(i).get("artefacts.drop_rate");
            artefactObjTmp.power = (int) sql.getResultSet().get(i).get("artefacts.power");
            artefactObjTmp.precision = (int) sql.getResultSet().get(i).get("artefacts.precision");
            artefactObjTmp.toughness = (int) sql.getResultSet().get(i).get("artefacts.toughness");
            artefactObjTmp.vitality = (int) sql.getResultSet().get(i).get("artefacts.vitality");
            artefactObjTmp.condition = (int) sql.getResultSet().get(i).get("artefacts.condition");
            artefactObjTmp.healing = (int) sql.getResultSet().get(i).get("artefacts.healing");
            artefactObjTmp.ferocity = (int) sql.getResultSet().get(i).get("artefacts.ferocity");
            artefactObjTmp.description = (String) sql.getResultSet().get(i).get("artefacts.description");

            artefactObjTmp.quality.id = (int) sql.getResultSet().get(i).get("qualities.id");
            artefactObjTmp.quality.name = (String) sql.getResultSet().get(i).get("qualities.name");
            artefactObjTmp.quality.power = (int) sql.getResultSet().get(i).get("qualities.power");

            artefactObjTmp.collection.id = (int) sql.getResultSet().get(i).get("collections.id");
            artefactObjTmp.collection.name = (String) sql.getResultSet().get(i).get("collections.name");
            artefactObjTmp.collection.title_id = (int) sql.getResultSet().get(i).get("collections.title_id");

            artefactObjTmp.collection.title.id = (int) sql.getResultSet().get(i).get("titles.id");
            artefactObjTmp.collection.title.name = (String) sql.getResultSet().get(i).get("titles.name");
            artefactObjTmp.collection.title.description = (String) sql.getResultSet().get(i).get("titles.description");

            artefactObjTmp.type.id = (int) sql.getResultSet().get(i).get("types.id");
            artefactObjTmp.type.name = (String) sql.getResultSet().get(i).get("types.name");
            artefactObj.add(artefactObjTmp);
        }
    }

    public ArrayList<ArtefactObj> getArtefactObj() {
        return artefactObj;
    }
}
