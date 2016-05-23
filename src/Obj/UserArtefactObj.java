package Obj;

/**
 * Created by teddy on 07/05/2016.
 */
public class UserArtefactObj {
    public int id;
    public int user_id;
    public int artefact_id;

    public UserObj user = new UserObj();
    public ArtefactObj artefact = new ArtefactObj();
}
