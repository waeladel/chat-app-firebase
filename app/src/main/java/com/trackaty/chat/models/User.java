package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User  implements Parcelable{

    private Long created;

    private String name;
    private String avatar;
    private String coverImage;
    private String biography;
    private int loveCounter;
    private int PickupCounter;
    private String relationship;
    private String interestedIn;

    public String gender;
    public Long age;
    public String horoscope;
    public String nationality;
    public String lives;
    public String hometown;
    public String religion;
    public String politics;
    public String work;
    public String college;
    public String school;
    public Boolean smoke;
    public Boolean shisha;
    public Boolean drugs;
    public Boolean drink;
    public Boolean athlete;
    public Boolean gamer;
    public Boolean travel;
    public Boolean cook;
    public Boolean read;

    // startedAt: firebase.database.ServerValue.TIMESTAMP
    //private Date joined;// anotation to put server timestamp

    public User() {

    }

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            created = null;
        } else {
            created = in.readLong();
        }
        name = in.readString();
        avatar = in.readString();
        coverImage = in.readString();
        biography = in.readString();
        loveCounter = in.readInt();
        PickupCounter = in.readInt();
        relationship = in.readString();
        interestedIn = in.readString();
        gender = in.readString();
        if (in.readByte() == 0) {
            age = null;
        } else {
            age = in.readLong();
        }
        horoscope = in.readString();
        nationality = in.readString();
        lives = in.readString();
        hometown = in.readString();
        religion = in.readString();
        politics = in.readString();
        work = in.readString();
        college = in.readString();
        school = in.readString();
        byte tmpSmoke = in.readByte();
        smoke = tmpSmoke == 0 ? null : tmpSmoke == 1;
        byte tmpShisha = in.readByte();
        shisha = tmpShisha == 0 ? null : tmpShisha == 1;
        byte tmpDrugs = in.readByte();
        drugs = tmpDrugs == 0 ? null : tmpDrugs == 1;
        byte tmpDrink = in.readByte();
        drink = tmpDrink == 0 ? null : tmpDrink == 1;
        byte tmpAthlete = in.readByte();
        athlete = tmpAthlete == 0 ? null : tmpAthlete == 1;
        byte tmpGamer = in.readByte();
        gamer = tmpGamer == 0 ? null : tmpGamer == 1;
        byte tmpTravel = in.readByte();
        travel = tmpTravel == 0 ? null : tmpTravel == 1;
        byte tmpCook = in.readByte();
        cook = tmpCook == 0 ? null : tmpCook == 1;
        byte tmpRead = in.readByte();
        read = tmpRead == 0 ? null : tmpRead == 1;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getLoveCounter() {
        return loveCounter;
    }

    public void setLoveCounter(int loveCounter) {
        this.loveCounter = loveCounter;
    }

    public int getPickupCounter() {
        return PickupCounter;
    }

    public void setPickupCounter(int pickupCounter) {
        PickupCounter = pickupCounter;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getInterestedIn() {
        return interestedIn;
    }

    public void setInterestedIn(String interestedIn) {
        this.interestedIn = interestedIn;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getHoroscope() {
        return horoscope;
    }

    public void setHoroscope(String horoscope) {
        this.horoscope = horoscope;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getLives() {
        return lives;
    }

    public void setLives(String lives) {
        this.lives = lives;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPolitics() {
        return politics;
    }

    public void setPolitics(String politics) {
        this.politics = politics;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Boolean getSmoke() {
        return smoke;
    }

    public void setSmoke(Boolean smoke) {
        this.smoke = smoke;
    }

    public Boolean getShisha() {
        return shisha;
    }

    public void setShisha(Boolean shisha) {
        this.shisha = shisha;
    }

    public Boolean getDrugs() {
        return drugs;
    }

    public void setDrugs(Boolean drugs) {
        this.drugs = drugs;
    }

    public Boolean getDrink() {
        return drink;
    }

    public void setDrink(Boolean drink) {
        this.drink = drink;
    }

    public Boolean getAthlete() {
        return athlete;
    }

    public void setAthlete(Boolean athlete) {
        this.athlete = athlete;
    }

    public Boolean getGamer() {
        return gamer;
    }

    public void setGamer(Boolean gamer) {
        this.gamer = gamer;
    }

    public Boolean getTravel() {
        return travel;
    }

    public void setTravel(Boolean travel) {
        this.travel = travel;
    }

    public Boolean getCook() {
        return cook;
    }

    public void setCook(Boolean cook) {
        this.cook = cook;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (created == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(created);
        }
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(coverImage);
        dest.writeString(biography);
        dest.writeInt(loveCounter);
        dest.writeInt(PickupCounter);
        dest.writeString(relationship);
        dest.writeString(interestedIn);
        dest.writeString(gender);
        if (age == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(age);
        }
        dest.writeString(horoscope);
        dest.writeString(nationality);
        dest.writeString(lives);
        dest.writeString(hometown);
        dest.writeString(religion);
        dest.writeString(politics);
        dest.writeString(work);
        dest.writeString(college);
        dest.writeString(school);
        dest.writeByte((byte) (smoke == null ? 0 : smoke ? 1 : 2));
        dest.writeByte((byte) (shisha == null ? 0 : shisha ? 1 : 2));
        dest.writeByte((byte) (drugs == null ? 0 : drugs ? 1 : 2));
        dest.writeByte((byte) (drink == null ? 0 : drink ? 1 : 2));
        dest.writeByte((byte) (athlete == null ? 0 : athlete ? 1 : 2));
        dest.writeByte((byte) (gamer == null ? 0 : gamer ? 1 : 2));
        dest.writeByte((byte) (travel == null ? 0 : travel ? 1 : 2));
        dest.writeByte((byte) (cook == null ? 0 : cook ? 1 : 2));
        dest.writeByte((byte) (read == null ? 0 : read ? 1 : 2));
    }
}
// [END blog_user_class]
