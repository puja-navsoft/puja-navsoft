package com.afieat.ini.models;

import java.util.List;

/**
 * Created by Harsh Rastogi on 17/01/18.
 */

public class SearchResponse {

    @Override
    public String toString() {
        return "SearchResponse{" +
                "results=" + results +
                '}';
    }

    private List<ResultsBean> results;

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * hits : [{"merchant_id":"80","status":"inactive","restaurant_name":"Aurach","restaurant_name_ar":"أوراش","contact_name":"Aurach","contact_phone":"5548966","contact_email":"Aurach@gmail.com","search_address":"البصرة","city_id":"2","cuisine":"[\"32\"]","ip_address":"172.31.16.52","restaurant_slug":"aurach-1","date_created":"2017-06-01 21:37:09","date_modified":"2017-12-22 10:30:30","objectID":"80","_highlightResult":{"restaurant_name":{"value":"<em>Aura<\/em>ch","matchLevel":"full","fullyHighlighted":false,"matchedWords":["turk"]},"restaurant_name_ar":{"value":"أوراش","matchLevel":"none","matchedWords":[]},"restaurant_slug":{"value":"<em>aura<\/em>ch-1","matchLevel":"full","fullyHighlighted":false,"matchedWords":["turk"]}}}]
         * nbHits : 6
         * page : 0
         * nbPages : 6
         * hitsPerPage : 1
         * processingTimeMS : 1
         * exhaustiveNbHits : true
         * query : turk
         * params : hitsPerPage=1&query=turk
         * index : restaurants
         */

        private int nbHits;
        private int page;
        private int nbPages;
        private int hitsPerPage;
        private int processingTimeMS;
        private boolean exhaustiveNbHits;
        private String query;
        private String params;
        private String index;
        private List<HitsBean> hits;

        public int getNbHits() {
            return nbHits;
        }

        public void setNbHits(int nbHits) {
            this.nbHits = nbHits;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getNbPages() {
            return nbPages;
        }

        public void setNbPages(int nbPages) {
            this.nbPages = nbPages;
        }

        public int getHitsPerPage() {
            return hitsPerPage;
        }

        public void setHitsPerPage(int hitsPerPage) {
            this.hitsPerPage = hitsPerPage;
        }

        public int getProcessingTimeMS() {
            return processingTimeMS;
        }

        public void setProcessingTimeMS(int processingTimeMS) {
            this.processingTimeMS = processingTimeMS;
        }

        public boolean isExhaustiveNbHits() {
            return exhaustiveNbHits;
        }

        public void setExhaustiveNbHits(boolean exhaustiveNbHits) {
            this.exhaustiveNbHits = exhaustiveNbHits;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public List<HitsBean> getHits() {
            return hits;
        }

        public void setHits(List<HitsBean> hits) {
            this.hits = hits;
        }

        public static class HitsBean {
            /**
             * merchant_id : 80
             * status : inactive
             * restaurant_name : Aurach
             * restaurant_name_ar : أوراش
             * contact_name : Aurach
             * contact_phone : 5548966
             * contact_email : Aurach@gmail.com
             * search_address : البصرة
             * city_id : 2
             * cuisine : ["32"]
             * ip_address : 172.31.16.52
             * restaurant_slug : aurach-1
             * date_created : 2017-06-01 21:37:09
             * date_modified : 2017-12-22 10:30:30
             * objectID : 80
             * _highlightResult : {"restaurant_name":{"value":"<em>Aura<\/em>ch","matchLevel":"full","fullyHighlighted":false,"matchedWords":["turk"]},"restaurant_name_ar":{"value":"أوراش","matchLevel":"none","matchedWords":[]},"restaurant_slug":{"value":"<em>aura<\/em>ch-1","matchLevel":"full","fullyHighlighted":false,"matchedWords":["turk"]}}
             */

            private int merchant_id;
            private String status;
            private String restaurant_name;
            private String restaurant_name_ar;
            private String contact_name;
            private String contact_phone;
            private String contact_email;
            private String search_address;
            private String city_id;
            private String cuisine;
            private String ip_address;
            private String restaurant_slug;
            private String date_created;
            private String date_modified;
            private String objectID;


            private String item_id;
            private String item_name;
            private String item_name_ar;
            private String cuisine_id;
            private String cuisine_name;
            private String cuisine_name_ar;
            private HighlightResultBean _highlightResult;

            public String getItem_id() {
                return item_id;
            }

            public void setItem_id(String item_id) {
                this.item_id = item_id;
            }

            public String getItem_name() {
                return item_name;
            }

            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }

            public String getItem_name_ar() {
                return item_name_ar;
            }

            public void setItem_name_ar(String item_name_ar) {
                this.item_name_ar = item_name_ar;
            }

            public String getCuisine_id() {
                return cuisine_id;
            }

            public void setCuisine_id(String cuisine_id) {
                this.cuisine_id = cuisine_id;
            }

            public String getCuisine_name() {
                return cuisine_name;
            }

            public void setCuisine_name(String cuisine_name) {
                this.cuisine_name = cuisine_name;
            }

            public String getCuisine_name_ar() {
                return cuisine_name_ar;
            }

            public void setCuisine_name_ar(String cuisine_name_ar) {
                this.cuisine_name_ar = cuisine_name_ar;
            }



            @Override
            public String toString() {
                return "HitsBean{" +
                        "merchant_id=" + merchant_id +
                        ", status='" + status + '\'' +
                        ", restaurant_name='" + restaurant_name + '\'' +
                        ", restaurant_name_ar='" + restaurant_name_ar + '\'' +
                        ", contact_name='" + contact_name + '\'' +
                        ", contact_phone='" + contact_phone + '\'' +
                        ", contact_email='" + contact_email + '\'' +
                        ", search_address='" + search_address + '\'' +
                        ", city_id='" + city_id + '\'' +
                        ", cuisine='" + cuisine + '\'' +
                        ", ip_address='" + ip_address + '\'' +
                        ", restaurant_slug='" + restaurant_slug + '\'' +
                        ", date_created='" + date_created + '\'' +
                        ", date_modified='" + date_modified + '\'' +
                        ", objectID='" + objectID + '\'' +
                        ", item_id='" + item_id + '\'' +
                        ", item_name='" + item_name + '\'' +
                        ", item_name_ar='" + item_name_ar + '\'' +
                        ", cuisine_id='" + cuisine_id + '\'' +
                        ", cuisine_name='" + cuisine_name + '\'' +
                        ", cuisine_name_ar='" + cuisine_name_ar + '\'' +
                        ", _highlightResult=" + _highlightResult +
                        '}';
            }

            public int getMerchant_id() {
                return merchant_id;
            }

            public void setMerchant_id(int merchant_id) {
                this.merchant_id = merchant_id;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getRestaurant_name() {
                return restaurant_name;
            }

            public void setRestaurant_name(String restaurant_name) {
                this.restaurant_name = restaurant_name;
            }

            public String getRestaurant_name_ar() {
                return restaurant_name_ar;
            }

            public void setRestaurant_name_ar(String restaurant_name_ar) {
                this.restaurant_name_ar = restaurant_name_ar;
            }

            public String getContact_name() {
                return contact_name;
            }

            public void setContact_name(String contact_name) {
                this.contact_name = contact_name;
            }

            public String getContact_phone() {
                return contact_phone;
            }

            public void setContact_phone(String contact_phone) {
                this.contact_phone = contact_phone;
            }

            public String getContact_email() {
                return contact_email;
            }

            public void setContact_email(String contact_email) {
                this.contact_email = contact_email;
            }

            public String getSearch_address() {
                return search_address;
            }

            public void setSearch_address(String search_address) {
                this.search_address = search_address;
            }

            public String getCity_id() {
                return city_id;
            }

            public void setCity_id(String city_id) {
                this.city_id = city_id;
            }

            public String getCuisine() {
                return cuisine;
            }

            public void setCuisine(String cuisine) {
                this.cuisine = cuisine;
            }

            public String getIp_address() {
                return ip_address;
            }

            public void setIp_address(String ip_address) {
                this.ip_address = ip_address;
            }

            public String getRestaurant_slug() {
                return restaurant_slug;
            }

            public void setRestaurant_slug(String restaurant_slug) {
                this.restaurant_slug = restaurant_slug;
            }

            public String getDate_created() {
                return date_created;
            }

            public void setDate_created(String date_created) {
                this.date_created = date_created;
            }

            public String getDate_modified() {
                return date_modified;
            }

            public void setDate_modified(String date_modified) {
                this.date_modified = date_modified;
            }

            public String getObjectID() {
                return objectID;
            }

            public void setObjectID(String objectID) {
                this.objectID = objectID;
            }

            public HighlightResultBean get_highlightResult() {
                return _highlightResult;
            }

            public void set_highlightResult(HighlightResultBean _highlightResult) {
                this._highlightResult = _highlightResult;
            }

            public static class HighlightResultBean {
                /**
                 * restaurant_name : {"value":"<em>Aura<\/em>ch","matchLevel":"full","fullyHighlighted":false,"matchedWords":["turk"]}
                 * restaurant_name_ar : {"value":"أوراش","matchLevel":"none","matchedWords":[]}
                 * restaurant_slug : {"value":"<em>aura<\/em>ch-1","matchLevel":"full","fullyHighlighted":false,"matchedWords":["turk"]}
                 */

                private RestaurantNameBean restaurant_name;
                private RestaurantNameArBean restaurant_name_ar;
                private RestaurantSlugBean restaurant_slug;

                public RestaurantNameBean getRestaurant_name() {
                    return restaurant_name;
                }

                public void setRestaurant_name(RestaurantNameBean restaurant_name) {
                    this.restaurant_name = restaurant_name;
                }

                public RestaurantNameArBean getRestaurant_name_ar() {
                    return restaurant_name_ar;
                }

                public void setRestaurant_name_ar(RestaurantNameArBean restaurant_name_ar) {
                    this.restaurant_name_ar = restaurant_name_ar;
                }

                public RestaurantSlugBean getRestaurant_slug() {
                    return restaurant_slug;
                }

                public void setRestaurant_slug(RestaurantSlugBean restaurant_slug) {
                    this.restaurant_slug = restaurant_slug;
                }

                public static class RestaurantNameBean {
                    /**
                     * value : <em>Aura</em>ch
                     * matchLevel : full
                     * fullyHighlighted : false
                     * matchedWords : ["turk"]
                     */

                    private String value;
                    private String matchLevel;
                    private boolean fullyHighlighted;
                    private List<String> matchedWords;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public String getMatchLevel() {
                        return matchLevel;
                    }

                    public void setMatchLevel(String matchLevel) {
                        this.matchLevel = matchLevel;
                    }

                    public boolean isFullyHighlighted() {
                        return fullyHighlighted;
                    }

                    public void setFullyHighlighted(boolean fullyHighlighted) {
                        this.fullyHighlighted = fullyHighlighted;
                    }

                    public List<String> getMatchedWords() {
                        return matchedWords;
                    }

                    public void setMatchedWords(List<String> matchedWords) {
                        this.matchedWords = matchedWords;
                    }
                }

                public static class RestaurantNameArBean {
                    /**
                     * value : أوراش
                     * matchLevel : none
                     * matchedWords : []
                     */

                    private String value;
                    private String matchLevel;
                    private List<?> matchedWords;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public String getMatchLevel() {
                        return matchLevel;
                    }

                    public void setMatchLevel(String matchLevel) {
                        this.matchLevel = matchLevel;
                    }

                    public List<?> getMatchedWords() {
                        return matchedWords;
                    }

                    public void setMatchedWords(List<?> matchedWords) {
                        this.matchedWords = matchedWords;
                    }
                }

                public static class RestaurantSlugBean {
                    /**
                     * value : <em>aura</em>ch-1
                     * matchLevel : full
                     * fullyHighlighted : false
                     * matchedWords : ["turk"]
                     */

                    private String value;
                    private String matchLevel;
                    private boolean fullyHighlighted;
                    private List<String> matchedWords;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public String getMatchLevel() {
                        return matchLevel;
                    }

                    public void setMatchLevel(String matchLevel) {
                        this.matchLevel = matchLevel;
                    }

                    public boolean isFullyHighlighted() {
                        return fullyHighlighted;
                    }

                    public void setFullyHighlighted(boolean fullyHighlighted) {
                        this.fullyHighlighted = fullyHighlighted;
                    }

                    public List<String> getMatchedWords() {
                        return matchedWords;
                    }

                    public void setMatchedWords(List<String> matchedWords) {
                        this.matchedWords = matchedWords;
                    }
                }
            }
        }
    }
}
