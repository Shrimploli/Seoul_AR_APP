package www.shrimp.org;

public class Upload {
    private String mName, mImageUri, mDesc, mTime;

    public Upload() {

    }

    public Upload(String name, String imageUrl, String desc, String time) {

        if (time.trim().equals("")) {
            time = "일정이 없습니다.";
        }

        if (desc.trim().equals("")) {

            desc = "소개가 없습니다.";
        }

        if (name.trim().equals("")) {
            name = "No name";
        }

        mName = name;
        mDesc = desc;
        mTime = time;
        mImageUri = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public void setImageUri(String imageUri) {
        mImageUri = imageUri;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

}
