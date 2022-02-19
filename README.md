### 一个springboot项目的新手demo

### 环境
```$xslt
环境：window11 + jdk8
开发软件：idea
连接数据源：es
```

### index示例
```$xslt
PUT /test
{
  "settings": {
    "analysis": {
      "analyzer": {
        "ngram_analyzer":{
          "tokenizer":"ngram_analyzer"
        }
      },
      "tokenizer":{
        "ngram_analyzer":{
          "type":"ngram",
          "min_gram":1,
          "max_gram":1,
          "token_chars":[
            "letter",
            "digit",
            "symbol",
            "punctuation"
            ]
        }
      }
    }
  }, 
  "mappings": {
    "properties": {
      "id": {
        "type": "keyword"
      },
      "name": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        },
        "analyzer": "ngram_analyzer"
      }
    }
  }
}
``` 

### 功能说明
```$xslt
es简单搜索功能
```

### 难点说明
```$xslt
es的难点不在搜索、上面；主要在于环境搭建与es的语句学习，故只是简单的一个demo。
如果想要学好es，建议es与kibana搭建一下，好好熟悉一下
```