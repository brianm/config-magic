package org.skife.config;

interface Config3
{
    // required
    @Config("option")
    public String getOption();

    public abstract String getOption2();
//
//    @Config("option3")
//    public abstract String getOption3();
//
//    public String getOption4()
//    {
//        return "option4 default";
//    }
}