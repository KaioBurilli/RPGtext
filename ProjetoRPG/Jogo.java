import java.util.*;

// ----- CLASSE ITEM -----
class Item implements Comparable<Item>, Cloneable {
private String nome;
private String descricao;
private String efeito; // exemplo: "cura", "ataque", "katana"
private int quantidade;
private int bonusAtaque; // Para katanas que aumentam ataque

public Item(String nome, String descricao, String efeito, int quantidade) {
this(nome, descricao, efeito, quantidade, 0);
}

public Item(String nome, String descricao, String efeito, int quantidade, int bonusAtaque) {
this.nome = nome;
this.descricao = descricao;
this.efeito = efeito;
this.quantidade = quantidade;
this.bonusAtaque = bonusAtaque;
}

public Item(Item outro) {
this.nome = outro.nome;
this.descricao = outro.descricao;
this.efeito = outro.efeito;
this.quantidade = outro.quantidade;
this.bonusAtaque = outro.bonusAtaque;
}

public String getNome() {
return nome;
}

public String getDescricao() {
return descricao;
}

public String getEfeito() {
return efeito;
}

public int getQuantidade() {
return quantidade;
}

public int getBonusAtaque() {
return bonusAtaque;
}

public void setQuantidade(int q) {
quantidade = q;
}

@Override
public boolean equals(Object obj) {
if (!(obj instanceof Item))
return false;
Item outro = (Item) obj;
return this.nome.equalsIgnoreCase(outro.nome);
}

@Override
public int compareTo(Item outro) {
return this.nome.compareToIgnoreCase(outro.nome);
}

@Override
public Item clone() {
return new Item(this);
}
}

// ----- CLASSE INVENTARIO -----
class Inventario implements Cloneable {
private List<Item> itens;

public Inventario() {
itens = new ArrayList<>();
}

public Inventario(Inventario outro) {
itens = new ArrayList<>();
for (Item i : outro.itens) {
itens.add(i.clone());
}
}

public void adicionarItem(Item item) {
for (Item i : itens) {
if (i.equals(item)) {
i.setQuantidade(i.getQuantidade() + item.getQuantidade());
return;
}
}
itens.add(item.clone());
}

public boolean removerItem(Item item, int quantidade) {
Iterator<Item> it = itens.iterator();
while (it.hasNext()) {
Item i = it.next();
if (i.equals(item)) {
if (i.getQuantidade() >= quantidade) {
i.setQuantidade(i.getQuantidade() - quantidade);
if (i.getQuantidade() == 0) {
it.remove();
}
return true;
}
return false;
}
}
return false;
}

public List<Item> listarItens() {
itens.sort(Comparator.naturalOrder());
return Collections.unmodifiableList(itens);
}

public boolean isVazio() {
return itens.isEmpty();
}

@Override
public Inventario clone() {
return new Inventario(this);
}
}

// ----- CLASSE ABSTRATA PERSONAGEM -----
abstract class Personagem {
protected String nome;
protected int pontosVidaBase;
protected int pontosVidaAtual;
protected int ataqueBase;
protected int defesa; // porcentagem de redução de dano (ex: 25 para 25%)
protected int nivel;
protected Inventario inventario;
protected List<Item> armasEquipada;

public Personagem() {
this.nome = "SemNome";
this.pontosVidaBase = 100;
this.pontosVidaAtual = pontosVidaBase;
this.ataqueBase = 10;
this.defesa = 25;
this.nivel = 1;
this.inventario = new Inventario();
this.armasEquipada = new ArrayList<>();
}

public Personagem(String nome, int pv, int atk, int def, int nivel) {
this.nome = nome;
this.pontosVidaBase = pv;
this.pontosVidaAtual = pv;
this.ataqueBase = atk;
this.defesa = def;
this.nivel = nivel;
this.inventario = new Inventario();
this.armasEquipada = new ArrayList<>();
}

public Personagem(Personagem outro) {
this.nome = outro.nome;
this.pontosVidaBase = outro.pontosVidaBase;
this.pontosVidaAtual = outro.pontosVidaAtual;
this.ataqueBase = outro.ataqueBase;
this.defesa = outro.defesa;
this.nivel = outro.nivel;
this.inventario = outro.inventario.clone();
this.armasEquipada = new ArrayList<>();
for (Item i : outro.armasEquipada) {
this.armasEquipada.add(i.clone());
}
}

public String getNome() {
return nome;
}

public int getPontosVida() {
return pontosVidaAtual;
}

public void setPontosVida(int pv) {
pontosVidaAtual = Math.min(Math.max(pv, 0), pontosVidaBase);
}

public int getAtaqueBase() {
return ataqueBase;
}

public int getDefesa() {
return defesa;
}

public int getNivel() {
return nivel;
}

public Inventario getInventario() {
return inventario;
}

public boolean estaVivo() {
return pontosVidaAtual > 0;
}

public void equiparArma(Item katana) {
armasEquipada.add(katana.clone());
}

public void removerArma(Item katana) {
armasEquipada.removeIf(i -> i.equals(katana));
}

public int getAtaqueTotal() {
int ataque = ataqueBase;
for (Item katana : armasEquipada) {
ataque += katana.getBonusAtaque();
}
return ataque;
}

// Aplica dano considerando a defesa (reduz em porcentagem)
public void receberDano(int dano) {
int danoReduzido = (int) Math.ceil(dano * (1 - defesa / 100.0));
setPontosVida(pontosVidaAtual - danoReduzido);
}
}

// ----- SUBCLASSES GUERREIRO, MAGO, ARQUEIRO -----
class Guerreiro extends Personagem {
public Guerreiro() {
super("Guerreiro", 130, 15, 25, 1);
inventario.adicionarItem(new Item("Poção de Cura", "Restaura 20 de vida", "cura", 2));
inventario.adicionarItem(new Item("Poção de Ataque", "Aumenta ataque em 50% no próximo ataque", "ataque", 1));
}

public Guerreiro(String nome) {
super(nome, 130, 15, 25, 1);
inventario.adicionarItem(new Item("Poção de Cura", "Restaura 20 de vida", "cura", 2));
inventario.adicionarItem(new Item("Poção de Ataque", "Aumenta ataque em 50% no próximo ataque", "ataque", 1));
}

public Guerreiro(Guerreiro outro) {
super(outro);
}
}

class Mago extends Personagem {
private int vidaNoInicioDaBatalha;

public Mago() {
super("Mago", 100, (int) (20 * 1.5), 25, 1);
inventario.adicionarItem(new Item("Poção de Cura", "Restaura 20 de vida", "cura", 2));
inventario.adicionarItem(new Item("Poção de Ataque", "Aumenta ataque em 50% no próximo ataque", "ataque", 1));
vidaNoInicioDaBatalha = pontosVidaAtual;
}

public Mago(String nome) {
super(nome, 100, (int) (20 * 1.5), 25, 1);
inventario.adicionarItem(new Item("Poção de Cura", "Restaura 20 de vida", "cura", 2));
inventario.adicionarItem(new Item("Poção de Ataque", "Aumenta ataque em 50% no próximo ataque", "ataque", 1));
vidaNoInicioDaBatalha = pontosVidaAtual;
}

public Mago(Mago outro) {
super(outro);
this.vidaNoInicioDaBatalha = outro.vidaNoInicioDaBatalha;
}

@Override
public int getAtaqueTotal() {
int ataque = super.getAtaqueTotal();
// Já calculado no construtor o aumento de 50%
return ataque;
}

// Cura 5 de vida quando ataca, se não sofreu dano
public int atacarComCura(boolean ataqueBonusAtivo) {
int ataque = getAtaqueTotal();
if (ataqueBonusAtivo) {
ataque = (int) (ataque * 1.5);
}
// Se tomou dano (vida atual < vida inicial), cura 5 de vida
if (pontosVidaAtual < vidaNoInicioDaBatalha) {
setPontosVida(pontosVidaAtual + 5);
System.out.println(nome + " se cura 5 pontos de vida após o ataque!");
}
return ataque;
}

public void resetVidaNoInicio() {
vidaNoInicioDaBatalha = pontosVidaAtual;
}
}

class Arqueiro extends Personagem {
private Random rand = new Random();

public Arqueiro() {
super("Arqueiro", 90, (int) (18 * 1.65), 25, 1);
inventario.adicionarItem(new Item("Poção de Cura", "Restaura 20 de vida", "cura", 2));
inventario.adicionarItem(new Item("Poção de Ataque", "Aumenta ataque em 50% no próximo ataque", "ataque", 1));
}

public Arqueiro(String nome) {
super(nome, 90, (int) (18 * 1.65), 25, 1);
inventario.adicionarItem(new Item("Poção de Cura", "Restaura 20 de vida", "cura", 2));
inventario.adicionarItem(new Item("Poção de Ataque", "Aumenta ataque em 50% no próximo ataque", "ataque", 1));
}

public Arqueiro(Arqueiro outro) {
super(outro);
}

@Override
public void receberDano(int dano) {
// Chance de esquivar 30%
int chance = rand.nextInt(100) + 1;
if (chance <= 30) {
System.out.println(nome + " esquivou do ataque!");
return; // Não recebe dano
}
super.receberDano(dano);
}
}

// ----- CLASSE INIMIGO -----
class Inimigo extends Personagem {
public Inimigo(String nome, int pvBase, int atkBase, int defPorcentagem, int nivel) {
super(nome, pvBase, atkBase, defPorcentagem, nivel);
}

public Inimigo(Inimigo outro) {
super(outro);
}
}

// ----- CLASSE JOGO -----
public class Jogo {
private Scanner scanner = new Scanner(System.in);
private Personagem jogador;
private Random rand = new Random();
private boolean ataqueBonusAtivo = false; // Para controlar poção de ataque

private int etapaHistoria = 0; // 0: inicio, 1: pós ninja, 2: pós guarda-costas, 3: samurai

public Jogo() {
System.out.println("Bem-vindo ao RPG do Guerreiro Oriental contra Onis!");
System.out.print("Digite o nome do seu personagem: ");
String nome = scanner.nextLine();

System.out.println("\nApós tanta luta, você decide se aventurar e largar sua cidade natal!");
System.out.println("Durante sua aventura, você encontra um templo abandonado...");

System.out.println("\nEscolha sua classe:");
System.out.println("1. Guerreiro");
System.out.println("2. Mago");
System.out.println("3. Arqueiro");
System.out.print("Sua escolha: ");

int classe = lerInt(1, 3);
switch (classe) {
case 1:
jogador = new Guerreiro(nome);
break;
case 2:
jogador = new Mago(nome);
break;
case 3:
jogador = new Arqueiro(nome);
break;
}

System.out.println("Você criou o " + jogador.getNome() + " da classe " + jogador.getClass().getSimpleName());
menuPrincipal();
}

private void menuPrincipal() {
while (true) {
System.out.println("\nMenu Principal:");
System.out.println("1. Explorar");
System.out.println("2. Ver Inventário");
System.out.println("3. Sair do Jogo");
System.out.print("Escolha: ");
int opc = lerInt(1, 3);

if (opc == 1) {
explorar();
} else if (opc == 2) {
mostrarInventario();
} else {
System.out.println("Saindo do jogo... Até mais!");
break;
}
}
}

private void explorar() {
System.out.println("\nExplorando...");
int evento = rand.nextInt(3); // 0: encontro inimigo, 1: encontrar poção, 2: encontrar arma

if (evento == 0) {
Inimigo inimigo = criarInimigoAleatorio();
System.out.println("Um inimigo aparece! É o " + inimigo.getNome());
batalha(inimigo);
} else if (evento == 1) {
encontrarPocao();
} else {
encontrarArma();
}
}

private Inimigo criarInimigoAleatorio() {
int escolha = rand.nextInt(3);
if (escolha == 0) {
return new Inimigo("Ninja", 90, 18, 20, 1);
} else if (escolha == 1) {
return new Inimigo("Guarda-Costas", 130, 15, 30, 1);
} else {
return new Inimigo("Samurai", 120, 25, 25, 1);
}
}

private void encontrarPocao() {
System.out.println("Você encontrou uma Poção de Cura! +1 ao inventário.");
jogador.getInventario().adicionarItem(new Item("Poção de Cura", "Restaura 20 de vida", "cura", 1));
}

private void encontrarArma() {
System.out.println("Você encontrou uma Katana!");
// Katana que aumenta ataque em 10
Item katana = new Item("Katana", "Aumenta ataque em 10", "katana", 1, 10);
jogador.getInventario().adicionarItem(katana);
}

private void mostrarInventario() {
Inventario inv = jogador.getInventario();
if (inv.isVazio()) {
System.out.println("Inventário vazio.");
return;
}
System.out.println("Itens no inventário:");
List<Item> itens = inv.listarItens();
for (int i = 0; i < itens.size(); i++) {
Item item = itens.get(i);
System.out.printf("%d. %s (%d) - %s\n", i + 1, item.getNome(), item.getQuantidade(), item.getDescricao());
}
System.out.println("Digite o número do item para usar/equipar ou 0 para voltar:");
int escolha = lerInt(0, itens.size());
if (escolha == 0) return;

Item selecionado = itens.get(escolha - 1);
usarItem(selecionado);
}

private void usarItem(Item item) {
switch (item.getEfeito()) {
case "cura":
if (item.getQuantidade() > 0) {
jogador.setPontosVida(jogador.getPontosVida() + 20);
System.out.println("Você usou uma Poção de Cura. Vida restaurada para " + jogador.getPontosVida());
jogador.getInventario().removerItem(item, 1);
}
break;
case "ataque":
if (item.getQuantidade() > 0) {
ataqueBonusAtivo = true;
System.out.println("Você usou uma Poção de Ataque. Próximo ataque com +50% de dano.");
jogador.getInventario().removerItem(item, 1);
}
break;
case "katana":
// Equipa a katana
jogador.equiparArma(item);
jogador.getInventario().removerItem(item, 1);
System.out.println("Katana equipada. Ataque aumentado em 10.");
break;
default:
System.out.println("Item não utilizável.");
}
}

private void batalha(Inimigo inimigo) {
System.out.println("Início da batalha: " + jogador.getNome() + " vs " + inimigo.getNome());
// Reset vida inicial do mago para cura
if (jogador instanceof Mago) {
((Mago) jogador).resetVidaNoInicio();
}

while (jogador.estaVivo() && inimigo.estaVivo()) {
System.out.println("\nSua vida: " + jogador.getPontosVida() + "/" + jogador.pontosVidaBase);
System.out.println("Vida inimiga: " + inimigo.getPontosVida() + "/" + inimigo.pontosVidaBase);
System.out.println("Escolha ação:");
System.out.println("1. Atacar");
System.out.println("2. Usar item");
System.out.println("3. Fugir");

int escolha = lerInt(1, 3);

if (escolha == 1) {
int ataqueJogador;
if (jogador instanceof Mago) {
ataqueJogador = ((Mago) jogador).atacarComCura(ataqueBonusAtivo);
if (ataqueBonusAtivo) {
ataqueBonusAtivo = false;
System.out.println("Ataque aumentado em 50% por efeito da poção!");
}
} else {
ataqueJogador = jogador.getAtaqueTotal();
if (ataqueBonusAtivo) {
ataqueJogador = (int) (ataqueJogador * 1.5);
ataqueBonusAtivo = false;
System.out.println("Ataque aumentado em 50% por efeito da poção!");
}
}
System.out.println(jogador.getNome() + " ataca causando " + ataqueJogador + " de dano.");
inimigo.receberDano(ataqueJogador);
} else if (escolha == 2) {
mostrarInventario();
continue; // volta para o loop, não permite inimigo atacar ainda
} else {
System.out.println("Você fugiu da batalha!");
return;
}

if (!inimigo.estaVivo()) {
System.out.println("Você derrotou o " + inimigo.getNome() + "!");
recompensaPosBatalha(inimigo);
break;
}

// Ataque do inimigo
int ataqueInimigo = inimigo.getAtaqueTotal();
System.out.println(inimigo.getNome() + " ataca causando " + ataqueInimigo + " de dano.");
jogador.receberDano(ataqueInimigo);

if (!jogador.estaVivo()) {
System.out.println("Você foi derrotado...");
System.exit(0);
}
}
}

private void recompensaPosBatalha(Inimigo inimigo) {
if (inimigo.getNome().equalsIgnoreCase("Ninja")) {
Item katana = new Item("Katana Ninja", "Katana poderosa", "katana", 1, 15);
jogador.getInventario().adicionarItem(katana);
etapaHistoria = 1;
System.out.println("Você ganhou uma Katana Ninja!");
} else if (inimigo.getNome().equalsIgnoreCase("Guarda-Costas")) {
Item pocaoCura = new Item("Poção de Cura", "Restaura 20 de vida", "cura", 3);
jogador.getInventario().adicionarItem(pocaoCura);
etapaHistoria = 2;
System.out.println("Você recebeu 3 Poções de Cura!");
} else if (inimigo.getNome().equalsIgnoreCase("Samurai")) {
Item katana = new Item("Katana Samurai", "Katana lendária", "katana", 1, 20);
jogador.getInventario().adicionarItem(katana);
etapaHistoria = 3;
System.out.println("Você ganhou uma Katana Samurai!");
}
}

private int lerInt(int min, int max) {
int valor;
while (true) {
try {
valor = Integer.parseInt(scanner.nextLine());
if (valor < min || valor > max) {
System.out.print("Digite um número entre " + min + " e " + max + ": ");
} else {
return valor;
}
} catch (Exception e) {
System.out.print("Entrada inválida. Digite um número: ");
}
}
}

public static void main(String[] args) {
new Jogo();
}
}