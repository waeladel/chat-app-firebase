package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class User implements Parcelable{


    private String key;
    private Object created ;

    private String avatar;
    private String coverImage;
    private String name;
    private String biography;
    private int loveCounter;
    private int PickupCounter;
    private String relationship;
    private String interestedIn;

    public String gender;
    public Long birthDate;
    public String horoscope;
    public String lives;
    public String hometown;
    public String nationality;
    public String religion;
    public String politics;
    public String work;
    public String college;
    public String school;

    public Boolean smoke;
    public Boolean shisha;
    public Boolean drugs;
    public Boolean drink;
    public Boolean gamer;
    public Boolean cook;
    public Boolean read;
    public Boolean athlete;
    public Boolean travel;



    public String phone;
    public String facebook;
    public String instagram;
    public String twitter;
    public String snapchat;
    public String tumblr;
    public String pubg;
    public String vk;
    public String askfm;
    public String curiouscat;
    public String saraha;
    public String pinterest;
    public String soundcloud;
    public String spotify;
    public String anghami;
    public String twitch;
    public String youtube;
    public String linkedIn;
    public String wikipedia;
    public String website;



    // startedAt: firebase.database.ServerValue.TIMESTAMP
    //private Date joined;// anotation to put server timestamp

    public User() {
        created = ServerValue.TIMESTAMP;
    }

    protected User(Parcel in) {
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
            birthDate = null;
        } else {
            birthDate = in.readLong();
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
        phone = in.readString();
        facebook = in.readString();
        instagram = in.readString();
        twitter = in.readString();
        snapchat = in.readString();
        tumblr = in.readString();
        pubg = in.readString();
        vk = in.readString();
        askfm = in.readString();
        curiouscat = in.readString();
        saraha = in.readString();
        pinterest = in.readString();
        soundcloud = in.readString();
        spotify = in.readString();
        anghami = in.readString();
        twitch = in.readString();
        youtube = in.readString();
        linkedIn = in.readString();
        wikipedia = in.readString();
        website = in.readString();
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

    @Exclude
    public String getKey() { return key; }
    @Exclude
    public void setKey(String key) { this.key = key; }

    public Object getCreated() {
        return created;
    }

    @Exclude
    public long getCreatedLong() {
        return (long) created;
    }

    public void setCreated(Object created) {
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

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getSnapchat() {
        return snapchat;
    }

    public void setSnapchat(String snapchat) {
        this.snapchat = snapchat;
    }

    public String getTumblr() {
        return tumblr;
    }

    public void setTumblr(String tumblr) {
        this.tumblr = tumblr;
    }

    public String getPubg() {
        return pubg;
    }

    public void setPubg(String pubg) {
        this.pubg = pubg;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public String getAskfm() {
        return askfm;
    }

    public void setAskfm(String askfm) {
        this.askfm = askfm;
    }

    public String getCuriouscat() {
        return curiouscat;
    }

    public void setCuriouscat(String curiouscat) {
        this.curiouscat = curiouscat;
    }

    public String getSaraha() {
        return saraha;
    }

    public void setSaraha(String saraha) {
        this.saraha = saraha;
    }

    public String getPinterest() {
        return pinterest;
    }

    public void setPinterest(String pinterest) {
        this.pinterest = pinterest;
    }

    public String getSoundcloud() {
        return soundcloud;
    }

    public void setSoundcloud(String soundcloud) {
        this.soundcloud = soundcloud;
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    public String getAnghami() {
        return anghami;
    }

    public void setAnghami(String anghami) {
        this.anghami = anghami;
    }

    public String getTwitch() {
        return twitch;
    }

    public void setTwitch(String twitch) {
        this.twitch = twitch;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(coverImage);
        dest.writeString(biography);
        dest.writeInt(loveCounter);
        dest.writeInt(PickupCounter);
        dest.writeString(relationship);
        dest.writeString(interestedIn);
        dest.writeString(gender);
        if (birthDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(birthDate);
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
        dest.writeString(phone);
        dest.writeString(facebook);
        dest.writeString(instagram);
        dest.writeString(twitter);
        dest.writeString(snapchat);
        dest.writeString(tumblr);
        dest.writeString(pubg);
        dest.writeString(vk);
        dest.writeString(askfm);
        dest.writeString(curiouscat);
        dest.writeString(saraha);
        dest.writeString(pinterest);
        dest.writeString(soundcloud);
        dest.writeString(spotify);
        dest.writeString(anghami);
        dest.writeString(twitch);
        dest.writeString(youtube);
        dest.writeString(linkedIn);
        dest.writeString(wikipedia);
        dest.writeString(website);
    }

}
// [END blog_user_class]