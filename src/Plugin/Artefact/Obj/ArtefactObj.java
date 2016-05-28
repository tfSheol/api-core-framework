package Plugin.Artefact.Obj;

/**
 * Created by teddy on 07/05/2016.
 */
public class ArtefactObj {
    public int id;
    public int quality_id;
    public int collection_id;
    public int type_id;
    public String name;
    public int drop_rate;
    public int power;
    public int precision;
    public int toughness;
    public int vitality;
    public int condition;
    public int healing;
    public int ferocity;
    public String description;

    public QualityObj quality = new QualityObj();
    public CollectionObj collection = new CollectionObj();
    public TypeObj type = new TypeObj();
}
