package Plugin.Artefact.Model;

import Core.Database.SQLite;
import Core.Model;
import Obj.ArtefactObj;
import Tool.SQLGetRequest;

/**
 * Created by teddy on 07/05/2016.
 */
public class Codex extends Model {
    public Codex() {
        SQLite sql = new SQLite(SQLGetRequest.ARTEFACTS);
        sql.select();
        for (int i = 0; i < sql.getResultSet().size(); i++) {
            ArtefactObj artefactObj = new ArtefactObj();
            artefactObj.id = (int) sql.getResultSet().get(i).get("artefacts.id");
            artefactObj.quality_id = (int) sql.getResultSet().get(i).get("artefacts.quality_id");
            artefactObj.collection_id = (int) sql.getResultSet().get(i).get("artefacts.collection_id");
            artefactObj.type_id = (int) sql.getResultSet().get(i).get("artefacts.type_id");
            artefactObj.name = (String) sql.getResultSet().get(i).get("artefacts.name");
            artefactObj.drop_rate = (int) sql.getResultSet().get(i).get("artefacts.drop_rate");
            artefactObj.power = (int) sql.getResultSet().get(i).get("artefacts.power");
            artefactObj.precision = (int) sql.getResultSet().get(i).get("artefacts.precision");
            artefactObj.toughness = (int) sql.getResultSet().get(i).get("artefacts.toughness");
            artefactObj.vitality = (int) sql.getResultSet().get(i).get("artefacts.vitality");
            artefactObj.condition = (int) sql.getResultSet().get(i).get("artefacts.condition");
            artefactObj.healing = (int) sql.getResultSet().get(i).get("artefacts.healing");
            artefactObj.ferocity = (int) sql.getResultSet().get(i).get("artefacts.ferocity");
            artefactObj.description = (String) sql.getResultSet().get(i).get("artefacts.description");

            artefactObj.quality.id = (int) sql.getResultSet().get(i).get("qualities.id");
            artefactObj.quality.name = (String) sql.getResultSet().get(i).get("qualities.name");
            artefactObj.quality.power = (int) sql.getResultSet().get(i).get("qualities.power");

            artefactObj.collection.id = (int) sql.getResultSet().get(i).get("collections.id");
            artefactObj.collection.name = (String) sql.getResultSet().get(i).get("collections.name");
            artefactObj.collection.title_id = (int) sql.getResultSet().get(i).get("collections.title_id");

            artefactObj.collection.title.id = (int) sql.getResultSet().get(i).get("titles.id");
            artefactObj.collection.title.name = (String) sql.getResultSet().get(i).get("titles.name");
            artefactObj.collection.title.description = (String) sql.getResultSet().get(i).get("titles.description");

            artefactObj.type.id = (int) sql.getResultSet().get(i).get("types.id");
            artefactObj.type.name = (String) sql.getResultSet().get(i).get("types.name");
            data.add(artefactObj);
        }
    }
}
