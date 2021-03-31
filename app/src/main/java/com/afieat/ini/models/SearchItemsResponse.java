package com.afieat.ini.models;

import java.util.List;

/**
 * Created by Harsh Rastogi on 17/01/18.
 */

public class SearchItemsResponse {

    /**
     * hits : [{"item_id":"998","merchant_id":"36","item_name":"chicken steak","item_name_ar":"نفر ستيك دجاج ","item_description":"chicken steak","item_description_ar":"ستيك دجاج ","ip_address":"149.255.200.44","status":"publish","category":"[\"169\"]","price":"{\"86\":\"13000\"}","addon_item":"","ingredients":"","gallery_photo":"[\"1483616216-1.jpg\"]","photo":"1483616206-1.jpg","date_created":"2016-10-22 10:18:53","date_modified":"2017-01-05 14:37:07","objectID":"998","_highlightResult":{"item_name":{"value":"<em>chi<\/em>cken steak","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]},"item_name_ar":{"value":"نفر ستيك دجاج ","matchLevel":"none","matchedWords":[]},"item_description":{"value":"<em>chi<\/em>cken steak","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]},"item_description_ar":{"value":"ستيك دجاج ","matchLevel":"none","matchedWords":[]}}},{"item_id":"983","merchant_id":"36","item_name":"Chicken escalope","item_name_ar":"نفر سكالوب دجاج ","item_description":"Chicken escalope","item_description_ar":"سكالوب دجاج ","ip_address":"149.255.200.44","status":"publish","category":"[\"169\"]","price":"{\"86\":\"13000\"}","addon_item":"","ingredients":"","gallery_photo":"[\"1483616585-1.jpg\"]","photo":"1483616575-1.jpg","date_created":"2016-10-22 09:52:00","date_modified":"2017-01-05 14:43:21","objectID":"983","_highlightResult":{"item_name":{"value":"<em>Chi<\/em>cken escalope","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]},"item_name_ar":{"value":"نفر سكالوب دجاج ","matchLevel":"none","matchedWords":[]},"item_description":{"value":"<em>Chi<\/em>cken escalope","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]},"item_description_ar":{"value":"سكالوب دجاج ","matchLevel":"none","matchedWords":[]}}}]
     * nbHits : 1537
     * page : 0
     * nbPages : 500
     * hitsPerPage : 2
     * processingTimeMS : 1
     * exhaustiveNbHits : true
     * query : chi
     * params : hitsPerPage=2&query=chi
     * index : items
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
         * item_id : 998
         * merchant_id : 36
         * item_name : chicken steak
         * item_name_ar : نفر ستيك دجاج
         * item_description : chicken steak
         * item_description_ar : ستيك دجاج
         * ip_address : 149.255.200.44
         * status : publish
         * category : ["169"]
         * price : {"86":"13000"}
         * addon_item :
         * ingredients :
         * gallery_photo : ["1483616216-1.jpg"]
         * photo : 1483616206-1.jpg
         * date_created : 2016-10-22 10:18:53
         * date_modified : 2017-01-05 14:37:07
         * objectID : 998
         * _highlightResult : {"item_name":{"value":"<em>chi<\/em>cken steak","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]},"item_name_ar":{"value":"نفر ستيك دجاج ","matchLevel":"none","matchedWords":[]},"item_description":{"value":"<em>chi<\/em>cken steak","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]},"item_description_ar":{"value":"ستيك دجاج ","matchLevel":"none","matchedWords":[]}}
         */

        private String item_id;
        private String merchant_id;
        private String item_name;
        private String item_name_ar;
        private String item_description;
        private String item_description_ar;
        private String ip_address;
        private String status;
        private String category;
        private String price;
        private String addon_item;
        private String ingredients;
        private String gallery_photo;
        private String photo;
        private String date_created;
        private String date_modified;
        private String objectID;
        private HighlightResultBean _highlightResult;

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(String merchant_id) {
            this.merchant_id = merchant_id;
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

        public String getItem_description() {
            return item_description;
        }

        public void setItem_description(String item_description) {
            this.item_description = item_description;
        }

        public String getItem_description_ar() {
            return item_description_ar;
        }

        public void setItem_description_ar(String item_description_ar) {
            this.item_description_ar = item_description_ar;
        }

        public String getIp_address() {
            return ip_address;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getAddon_item() {
            return addon_item;
        }

        public void setAddon_item(String addon_item) {
            this.addon_item = addon_item;
        }

        public String getIngredients() {
            return ingredients;
        }

        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }

        public String getGallery_photo() {
            return gallery_photo;
        }

        public void setGallery_photo(String gallery_photo) {
            this.gallery_photo = gallery_photo;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
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
             * item_name : {"value":"<em>chi<\/em>cken steak","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]}
             * item_name_ar : {"value":"نفر ستيك دجاج ","matchLevel":"none","matchedWords":[]}
             * item_description : {"value":"<em>chi<\/em>cken steak","matchLevel":"full","fullyHighlighted":false,"matchedWords":["chi"]}
             * item_description_ar : {"value":"ستيك دجاج ","matchLevel":"none","matchedWords":[]}
             */

            private ItemNameBean item_name;
            private ItemNameArBean item_name_ar;
            private ItemDescriptionBean item_description;
            private ItemDescriptionArBean item_description_ar;

            public ItemNameBean getItem_name() {
                return item_name;
            }

            public void setItem_name(ItemNameBean item_name) {
                this.item_name = item_name;
            }

            public ItemNameArBean getItem_name_ar() {
                return item_name_ar;
            }

            public void setItem_name_ar(ItemNameArBean item_name_ar) {
                this.item_name_ar = item_name_ar;
            }

            public ItemDescriptionBean getItem_description() {
                return item_description;
            }

            public void setItem_description(ItemDescriptionBean item_description) {
                this.item_description = item_description;
            }

            public ItemDescriptionArBean getItem_description_ar() {
                return item_description_ar;
            }

            public void setItem_description_ar(ItemDescriptionArBean item_description_ar) {
                this.item_description_ar = item_description_ar;
            }

            public static class ItemNameBean {
                /**
                 * value : <em>chi</em>cken steak
                 * matchLevel : full
                 * fullyHighlighted : false
                 * matchedWords : ["chi"]
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

            public static class ItemNameArBean {
                /**
                 * value : نفر ستيك دجاج
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

            public static class ItemDescriptionBean {
                /**
                 * value : <em>chi</em>cken steak
                 * matchLevel : full
                 * fullyHighlighted : false
                 * matchedWords : ["chi"]
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

            public static class ItemDescriptionArBean {
                /**
                 * value : ستيك دجاج
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
        }
    }
}
