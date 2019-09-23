package com.trackaty.chat.models;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {


    private String key;
    private Object created;
    private Long lastOnline;

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


    //public Map<String, SocialObj> social = new HashMap<>();
    public SocialObj phone ;
    public SocialObj facebook ;
    public SocialObj instagram ;
    public SocialObj  twitter;
    public SocialObj snapchat ;
    public SocialObj tumblr ;
    public SocialObj pubg ;
    public SocialObj vk ;
    public SocialObj askfm ;
    public SocialObj curiouscat ;
    public SocialObj saraha ;
    public SocialObj pinterest ;
    public SocialObj soundcloud ;
    public SocialObj spotify ;
    public SocialObj anghami ;
    public SocialObj twitch ;
    public SocialObj youtube ;
    public SocialObj linkedIn ;
    public SocialObj wikipedia ;
    public SocialObj website ;

    public Map<String, Boolean> tokens = new HashMap<>();

    // startedAt: firebase.database.ServerValue.TIMESTAMP
    //private Date joined;// anotation to put server timestamp

    public User() {
        //this.created = ServerValue.TIMESTAMP;
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("created", ServerValue.TIMESTAMP);
        result.put("lastOnline", lastOnline);

        result.put("avatar", avatar);
        result.put("coverImage", coverImage);
        result.put("name", name);
        result.put("biography", biography);
        result.put("loveCounter", loveCounter);
        result.put("PickupCounter", PickupCounter);
        result.put("relationship", relationship);
        result.put("interestedIn", interestedIn);

        result.put("gender", gender);
        result.put("birthDate", birthDate);
        result.put("horoscope", horoscope);
        result.put("lives", lives);
        result.put("hometown", hometown);
        result.put("nationality", nationality);
        result.put("religion", religion);
        result.put("politics", politics);
        result.put("work", work);
        result.put("college", college);
        result.put("school", school);

        result.put("smoke", smoke);
        result.put("shisha", shisha);
        result.put("drugs", drugs);
        result.put("drink", drink);
        result.put("gamer", gamer);
        result.put("cook", cook);
        result.put("read", read);
        result.put("athlete", athlete);
        result.put("travel", travel);

        result.put("phone", phone );
        result.put("facebook", facebook );
        result.put("instagram", instagram );
        result.put("twitter", twitter );
        result.put("snapchat", snapchat );
        result.put("tumblr", tumblr );
        result.put("pubg", pubg );
        result.put("vk", vk );
        result.put("askfm", askfm );
        result.put("curiouscat", curiouscat );
        result.put("saraha", saraha);
        result.put("pinterest", pinterest);
        result.put("soundcloud", soundcloud);
        result.put("spotify", spotify);
        result.put("anghami", anghami);
        result.put("twitch", twitch);
        result.put("youtube", youtube);
        result.put("linkedIn", linkedIn);
        result.put("wikipedia", wikipedia);
        result.put("website", website);



        return result;
    }
    // [END post_to_map]

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

    public Long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Long lastOnline) {
        this.lastOnline = lastOnline;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public Boolean getGamer() {
        return gamer;
    }

    public void setGamer(Boolean gamer) {
        this.gamer = gamer;
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

    public Boolean getAthlete() {
        return athlete;
    }

    public void setAthlete(Boolean athlete) {
        this.athlete = athlete;
    }

    public Boolean getTravel() {
        return travel;
    }

    public void setTravel(Boolean travel) {
        this.travel = travel;
    }

    public SocialObj getPhone() {
        return phone;
    }

    public void setPhone(SocialObj phone) {
        this.phone = phone;
    }

    public SocialObj getFacebook() {
        return facebook;
    }

    public void setFacebook(SocialObj facebook) {
        this.facebook = facebook;
    }

    public SocialObj getInstagram() {
        return instagram;
    }

    public void setInstagram(SocialObj instagram) {
        this.instagram = instagram;
    }

    public SocialObj getTwitter() {
        return twitter;
    }

    public void setTwitter(SocialObj twitter) {
        this.twitter = twitter;
    }

    public SocialObj getSnapchat() {
        return snapchat;
    }

    public void setSnapchat(SocialObj snapchat) {
        this.snapchat = snapchat;
    }

    public SocialObj getTumblr() {
        return tumblr;
    }

    public void setTumblr(SocialObj tumblr) {
        this.tumblr = tumblr;
    }

    public SocialObj getPubg() {
        return pubg;
    }

    public void setPubg(SocialObj pubg) {
        this.pubg = pubg;
    }

    public SocialObj getVk() {
        return vk;
    }

    public void setVk(SocialObj vk) {
        this.vk = vk;
    }

    public SocialObj getAskfm() {
        return askfm;
    }

    public void setAskfm(SocialObj askfm) {
        this.askfm = askfm;
    }

    public SocialObj getCuriouscat() {
        return curiouscat;
    }

    public void setCuriouscat(SocialObj curiouscat) {
        this.curiouscat = curiouscat;
    }

    public SocialObj getSaraha() {
        return saraha;
    }

    public void setSaraha(SocialObj saraha) {
        this.saraha = saraha;
    }

    public SocialObj getPinterest() {
        return pinterest;
    }

    public void setPinterest(SocialObj pinterest) {
        this.pinterest = pinterest;
    }

    public SocialObj getSoundcloud() {
        return soundcloud;
    }

    public void setSoundcloud(SocialObj soundcloud) {
        this.soundcloud = soundcloud;
    }

    public SocialObj getSpotify() {
        return spotify;
    }

    public void setSpotify(SocialObj spotify) {
        this.spotify = spotify;
    }

    public SocialObj getAnghami() {
        return anghami;
    }

    public void setAnghami(SocialObj anghami) {
        this.anghami = anghami;
    }

    public SocialObj getTwitch() {
        return twitch;
    }

    public void setTwitch(SocialObj twitch) {
        this.twitch = twitch;
    }

    public SocialObj getYoutube() {
        return youtube;
    }

    public void setYoutube(SocialObj youtube) {
        this.youtube = youtube;
    }

    public SocialObj getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(SocialObj linkedIn) {
        this.linkedIn = linkedIn;
    }

    public SocialObj getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(SocialObj wikipedia) {
        this.wikipedia = wikipedia;
    }

    public SocialObj getWebsite() {
        return website;
    }

    public void setWebsite(SocialObj website) {
        this.website = website;
    }

    public Map<String, Boolean> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Boolean> tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return
                TextUtils.equals(avatar, user.avatar) &&
                TextUtils.equals(name, user.name) &&
                TextUtils.equals(biography, user.biography) &&
                TextUtils.equals(relationship, user.relationship) &&
                TextUtils.equals(interestedIn, user.interestedIn) &&
                TextUtils.equals(gender, user.gender) &&
                TextUtils.equals(horoscope, user.horoscope) &&
                (birthDate == user.birthDate || (birthDate!= null && birthDate.equals(user.birthDate))) &&
                (created == user.created || (created!=null && created.equals(user.created)));
    }

    @Override
    public int hashCode() {
        //return Objects.hash(created, avatar, name, biography, relationship, interestedIn, gender, birthDate, horoscope);
        int result = 1;
        result = 31 * result + (avatar == null ? 0 : avatar.hashCode());
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (biography == null ? 0 : biography.hashCode());
        result = 31 * result + (relationship == null ? 0 : relationship.hashCode());
        result = 31 * result + (interestedIn == null ? 0 : interestedIn.hashCode());
        result = 31 * result + (gender == null ? 0 : gender.hashCode());
        result = 31 * result + (horoscope == null ? 0 : horoscope.hashCode());
        result = 31 * result + (birthDate == null ? 0 : birthDate.hashCode());
        result = 31 * result + (created == null ? 0 : created.hashCode());
        return result;

    }
}
// [END blog_user_class]