CONVERT=convert 
DEST=../res/drawable
%.png: ../../svg_icons/%.svg; 
	$ONVERT} -background none $< $@
SVG = $(wildcard *.svg)
PNG = $(patsubst %.svg,$(DEST)/%.png,$(SVG))

all: $(PNG)
clean:
	rm -f $(PNG)
