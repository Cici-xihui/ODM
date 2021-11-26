# Multi-Label kNN classifier with Online Dual Memory on data stream

Due to an ever-increasing demand for analyzing the large volumes of information issuing from high-speed data streams, multi-label stream classification is replacing the traditional offline multi-label classification system and has thus become a focal point in recent years. In this paper, we propose a new algorithm for multi-label stream classification that relies on an Online Dual Memory (ODM). It uses a short-term memory in order to adapt quickly to changes within the data stream, and employs a long-term memory for recurring concepts. Furthermore, via a modification of Learning Vector Quantization, ODM is not only capable of classifying efficiently but also of maintaining a low computational complexity. Experiments were conducted so as to establish a comprehensive overview of its performance, which was achieved by comparing four methods on two different kinds of datasets (nine stationary datasets and four datasets with concept drift). Our results confirm the high accuracy and low time complexity of ODM, as well as its ability to cope with concept drift.

All experiments were implemented in MOA : https://moa.cms.waikato.ac.nz/ with jvm -v 11.0.4

## Experimental data
### Stationnaire stream data :
- 20NG
- Bookmarks
- Corel16k
- Enron
- IMDB
- Mediamill
- Ohsumed
- SLASHDOT
- Yeast

All data can be found on : http://www.uco.es/kdis/mllresources/

### Non-stationnaire stream data:
We have selected two generators (Random RBF and RandomTree) from the MOA environment and adapted them to generate multi-label data. 

# Multi-Label kNN classifier with Online Dual Memory on data stream
> <cite> Xihui WANG, Pascale KUNTZ, Frank MEYER and Vincent LEMAIRE</cite>
