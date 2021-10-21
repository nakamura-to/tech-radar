import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.github.doyaaaaaken:kotlin-csv-jvm:1.1.0")
    }
}

plugins {
    base
}

tasks {
    register("generate") {
        doLast {
            val rows = read()
            val html = build(rows)
            write(html)
        }
    }
}

fun read(): List<Entry> {
    val file = file("index.csv")
    val rows = csvReader().readAllWithHeader(file)
    return rows.map { 
        Entry(
            quadrant = it["quadrant"]!!.toInt(),
            ring = it["ring"]!!.toInt(),
            label = it["label"]!!,
            link = it["link"],
            active = it["active"]!!.toBoolean(),
            moved = it["moved"]!!.toInt()
        )
    }
}

fun write(html: String) {
    val file = file("docs/index.html")
    file.writeText(html)
}

fun build(entries: List<Entry>): String {
    return """
<html>

<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<title>Zalando Tech Radar</title>
<link rel="shortcut icon" href="https://www.zalando.de/favicon.ico">

<script src="https://d3js.org/d3.v4.min.js"></script>
<script src="radar.js"></script>

<link rel="stylesheet" href="radar.css">
</head>

<body>

<svg id="radar"></svg>

<script>
radar_visualization({
  svg_id: "radar",
  width: 1450,
  height: 1000,
  colors: {
    background: "#fff",
    grid: "#bbb",
    inactive: "#ddd"
  },
  title: "Zalando Tech Radar — 2021.06",
  quadrants: [
    { name: "Languages" },
    { name: "Infrastructure" },
    { name: "Datastores" },
    { name: "Data Management" },
  ],
  rings: [
    { name: "ADOPT", color: "#93c47d" },
    { name: "TRIAL", color: "#93d2c2" },
    { name: "ASSESS", color: "#fbdb84" },
    { name: "HOLD", color: "#efafa9" }
  ],
  print_layout: true,
  // zoomed_quadrant: 0,
  //ENTRIES
  entries: [
${entries.joinToString(separator = ",") { it.toJs() }}
  ]
  //ENTRIES
});
</script>

<table>
<tr>
<td>

<h3>What is the Tech Radar?</h3>

<p>
The Zalando Tech Radar is a list of technologies, complemented by an assessment result, called <em>ring assignment</em>. We use four rings with the following semantics:
</p>

<ul>
<li><strong>ADOPT</strong> &mdash; Technologies we have high confidence in to serve our purpose, also in large scale. Technologies with a usage culture in our Zalando production environment, low risk and recommended to be widely used.</li>
<li><strong>TRIAL</strong> &mdash; Technologies that we have seen work with success in project work to solve a real problem; first serious usage experience that confirm benefits and can uncover limitations. TRIAL technologies are slightly more risky; some engineers in our organization walked this path and will share knowledge and experiences.</li>
<li><strong>ASSESS</strong> &mdash; Technologies that are promising and have clear potential value-add for us; technologies worth to invest some research and prototyping efforts in to see if it has impact. ASSESS technologies have higher risks; they are often brand new and highly unproven in our organisation. You will find some engineers that have knowledge in the technology and promote it, you may even find teams that have started a prototyping effort.</li>
<li><strong>HOLD</strong> &mdash; Technologies not recommended to be used for new projects. Technologies that we think are not (yet) worth to (further) invest in. HOLD technologies should not be used for new projects, but usually can be continued for existing projects.</li>
</ul>

</td><td>

<h3>What is the purpose?</h3>

<p>
The Tech Radar is a tool to inspire and support Engineering teams at Zalando to pick the best technologies for new projects; it provides a platform to share knowledge and experience in technologies, to reflect on technology decisions and continuously evolve our technology landscape. Based on the <a href="https://www.thoughtworks.com/radar">pioneering work of ThoughtWorks</a>, our Tech Radar sets out the changes in technologies that are interesting in software development &mdash; changes that we think our engineering teams should pay attention to and use in their projects.
</p>

<h3>How do we maintain it?</h3>

<p>
The Tech Radar is maintained by our <em>Principal Engineers</em> &mdash; who faciliate and drive the technology selection discussions at Zalando across the Engineering Community. Assignment of technologies to rings is the outcome of ring change proposals, which are discussed and voted on. The Tech Radar is open for contribution for all Engineering teams at Zalando and depends on their active participation to share lessons learned, pitfalls, and contribute to good practices on using the technologies.<br/>
</p>

<p>
Check out our <a href="https://engineering.zalando.com/tags/tech-radar.html">Engineering Blog</a> for more information on how we approach Technology Selection and use the Tech Radar at Zalando.
</p>

<p>
<em>BTW, if you would like to create your own Tech Radar &mdash; we have <a href="https://github.com/zalando/tech-radar">open sourced the code</a> to generate this visualization.</em>
</p>

</td></tr>
</table>

</body>
</html>
"""
}

data class Entry(
    val quadrant: Int,
    val ring: Int,
    val label: String,
    val link: String? = null,
    val active: Boolean,
    val moved: Int,
)

fun Entry.toJs(): String {
    return """
    {
      "quadrant": $quadrant,
      "ring": $ring,
      "label": "$label",
      "active": $active,
      "moved": $moved
    }"""
}