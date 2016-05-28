package Plugin.Artefact.Obj;

/**
 * Created by teddy on 07/05/2016.
 */
public class FriendObj {
    public int id;
    public int user_id;
    public int friend_id;

    public UserObj user = new UserObj();
    public UserObj friend = new UserObj();
}
