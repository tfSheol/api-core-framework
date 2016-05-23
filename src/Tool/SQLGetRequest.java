package Tool;

/**
 * Created by teddy on 07/05/2016.
 */
public class SQLGetRequest {
    public static String ARTEFACTS = "SELECT artefacts.id'artefacts.id',\n" +
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
    public static String QUALITIES = "";
    public static String TYPES = "";

    public static String COLLECTIONS = "";
    public static String TITLES = "";

    public static String FRIENDS = "";
    public static String MAP_ARTEFACTS = "";
    public static String MODULE = "";
    public static String USER_ARTEFACTS = "";
    public static String USERS = "";
}
