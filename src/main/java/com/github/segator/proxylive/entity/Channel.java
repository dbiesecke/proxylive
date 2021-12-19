package com.github.segator.proxylive.entity;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Channel {
    private Integer number;
    private String name; //used by EPG matching
    private String id; //used by EPG matching
    private String epgID;
    private String logoURL;
    private File logoFile;
    private List<String> categories;
    private List<ChannelSource> sources;
    private String ffmpegParameters="";
    public Channel(){

    }
    public static Channel createFromChannel(Channel source){
        Channel channel = new Channel();
        channel.setId(source.getId());
        channel.setCategories(new ArrayList<>(source.getCategories()));
        channel.setEpgID(source.getEpgID());
        channel.setFfmpegParameters(source.getFfmpegParameters());
        channel.setName(source.getName());
        channel.setLogoFile(source.getLogoFile());
        channel.setNumber(source.getNumber());
        channel.setSources(new ArrayList<>(source.getSources()));
        channel.setLogoURL(source.getLogoURL());
        return channel;
    }


    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getEpgID() {
        return epgID;
    }

    public void setEpgID(String epgID) {
        this.epgID = epgID;
    }

    public File getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(File logoFile) {
        this.logoFile = logoFile;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<ChannelSource> getSources() {
        return sources;
    }

    public ChannelSource getSourceByPriority(int priority){
        Object[] orderedSources = getSources().stream().sorted(new Comparator<ChannelSource>() {
            @Override
            public int compare(ChannelSource o1, ChannelSource o2) {
                return o1.getPriority().compareTo(o2.getPriority());
            }
        }).toArray();
        if(priority> orderedSources.length){
            return null;
        }else {
            return (ChannelSource) orderedSources[priority-1];
        }

    }

    public void setSources(List<ChannelSource> sources) {
        this.sources = sources;
    }

    public String getFfmpegParameters() {
        return ffmpegParameters;
    }

    public void setFfmpegParameters(String ffmpegParameters) {
        this.ffmpegParameters = ffmpegParameters;
    }
}


