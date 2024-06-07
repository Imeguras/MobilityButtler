package models

type M2MCin struct {
	Cin ContentInstance `json:"m2m:cin"`
}

type ContentInstance struct {
	Con string `json:"con"`
	Cnf string `json:"cnf"`
	Ri  string `json:"ri"`
	Pi  string `json:"pi"`
	Rn  string `json:"rn"`
	Ct  string `json:"ct"`
	Lt  string `json:"lt"`
	Ty  int    `json:"ty"`
	Cs  int    `json:"cs"`
	St  int    `json:"st"`
	Et  string `json:"et"`
}
