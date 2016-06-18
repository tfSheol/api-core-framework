package Plugin.Artefact.Model;

import Core.Database.SQLRequest;
import Core.Model;
import Plugin.Artefact.Obj.ArtefactObj;

/**
 * Created by teddy on 07/05/2016.
 */
public class CodexModel extends Model {
    private static String ARTEFACTS = "SELECT artefacts.id'artefacts.id',\n" +
            "artefacts.quality_id'artefacts.quality_id',\n" +
            "artefacts.collection_id'artefacts.collection_id',\n" +
            "artefacts.type_id'artefacts.type_id',\n" +
            "artefacts.name'artefacts.name',\n" +
            "artefacts.drop_rate'artefacts.drop_rate',\n" +
            "artefacts.power'artefacts.power',\n" +
            "artefacts.precision'artefacts.precision',\n" +
            "artefacts.toughness'artefacts.toughness',\n" +
            "artefacts.vitality'artefacts.vitality',\n" +
            "artefacts.condition'artefacts.condition',\n" +
            "artefacts.healing'artefacts.healing',\n" +
            "artefacts.ferocity'artefacts.ferocity',\n" +
            "artefacts.description'artefacts.description',\n" +
            "qualities.id'qualities.id',\n" +
            "qualities.name'qualities.name',\n" +
            "qualities.power'qualities.power',\n" +
            "collections.id'collections.id',\n" +
            "collections.name'collections.name',\n" +
            "collections.title_id'collections.title_id',\n" +
            "titles.id'titles.id',\n" +
            "titles.description'titles.description',\n" +
            "titles.name'titles.name',\n" +
            "types.id'types.id',\n" +
            "types.name'types.name'\n" +
            "FROM artefacts, qualities, collections, types, titles \n" +
            "WHERE qualities.id=artefacts.quality_id\n" +
            "AND types.id=artefacts.type_id\n" +
            "AND collections.id=artefacts.collection_id\n" +
            "AND titles.id=collections.title_id";

    public CodexModel() {
        SQLRequest sql = new SQLRequest(ARTEFACTS);
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
