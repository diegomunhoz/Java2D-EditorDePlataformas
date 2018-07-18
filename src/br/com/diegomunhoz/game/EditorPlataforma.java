package br.com.diegomunhoz.game;

import br.com.diegomunhoz.core.DataManager;
import br.com.diegomunhoz.core.Game;
import br.com.diegomunhoz.core.InputManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorPlataforma extends Game {

    ArrayList<Entidade> entidades;
    Entidade entidadeSelecionada;
    boolean criandoEntidade;
    boolean gameOver;

    public EditorPlataforma() {
        entidades = new ArrayList<Entidade>();
        criandoEntidade = false;
        gameOver = false;
    }

    @Override
    public void onLoad() {
        carrega();
    }

    @Override
    public void onUnload() {
    }

    @Override
    public void onUpdate(int currentTick) {
        for (Entidade e : entidades) {
            e.update(currentTick);
        }
        if (InputManager.getInstance().isPressed(KeyEvent.VK_ESCAPE)) {
            terminate();
        }
        if (InputManager.getInstance().isPressed(KeyEvent.VK_DELETE)) {
            if (entidadeSelecionada != null) {
                entidades.remove(entidadeSelecionada);
                entidadeSelecionada = null;
            }
        }
        if (InputManager.getInstance().isPressed(KeyEvent.VK_S)) {
            salva();
        }
        if (InputManager.getInstance().isPressed(KeyEvent.VK_C)) {
            carrega();
        }
        if (criandoEntidade) {
            if (InputManager.getInstance().isMousePressed(
                    MouseEvent.BUTTON1)) {
                entidadeSelecionada.pos.width = InputManager.getInstance().
                        getMouseX() - entidadeSelecionada.pos.x;
                entidadeSelecionada.pos.height = InputManager.getInstance().
                        getMouseY() - entidadeSelecionada.pos.y;
            } else {
                if (entidadeSelecionada.pos.width < 10 || entidadeSelecionada.pos.height
                        < 10) {
                    entidades.remove(entidadeSelecionada);
                } else {
                    entidadeSelecionada.init();
                }
                criandoEntidade = false;
                entidadeSelecionada = null;
            }
        } else if (InputManager.getInstance().isMousePressed(
                MouseEvent.BUTTON1)) {
            Entidade temp = null;
            for (Entidade e : entidades) {
                if (e.pos.contains(InputManager.getInstance().getMousePos())) {
                    temp = e;
                }
            }
            if (temp != null) {
                entidadeSelecionada = temp;
            } else {
                entidadeSelecionada = new EntidadePlataforma(
                        InputManager.getInstance().getMouseX(),
                        InputManager.getInstance().getMouseY(), 10, 10);
                entidadeSelecionada.init();
                entidades.add(entidadeSelecionada);
                criandoEntidade = true;
            }
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        g.setColor(Color.darkGray);
        g.fillRect(0, 0, 800, 600);
        for (Entidade e : entidades) {
            e.render(g);
        }
        g.setColor(Color.white);
        if (entidadeSelecionada != null) {
            g.draw(entidadeSelecionada.pos);
        }
        if (criandoEntidade) {
            g.drawString("CRIANDO PLATAFORMA", 400, 20);
        }
        g.drawString("[S] Salvar    [C] Carregar    [ESC] Sair", 580, 20);
    }

    public void salva() {
        try {
            DataManager dm
                    = new DataManager(File.separator + "editor.save");
            dm.write("plataformas", entidades.size());
            for (int i = 0; i < entidades.size(); i++) {
                Entidade e = entidades.get(i);
                dm.write("plataforma." + i + ".x", (int) e.pos.x);
                dm.write("plataforma." + i + ".y", (int) e.pos.y);
                dm.write("plataforma." + i + ".width", (int) e.pos.width);
                dm.write("plataforma." + i + ".height", (int) e.pos.height);
            }
            dm.save();
        } catch (IOException ex) {
            Logger.getLogger(EditorPlataforma.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void carrega() {
        try {
            DataManager dm
                    = new DataManager(File.separator + "editor.save");
            entidades.clear();
            int qtd = 0;
            qtd = dm.read("plataformas", qtd);
            for (int i = 0; i < qtd; i++) {
                Entidade e = new EntidadePlataforma(0, 0, 0, 0);
                e.pos.x = dm.read("plataforma." + i + ".x", (int) e.pos.x);
                e.pos.y = dm.read("plataforma." + i + ".y", (int) e.pos.y);
                e.pos.width = dm.read("plataforma." + i + ".width",
                        (int) e.pos.width);
                e.pos.height = dm.read("plataforma." + i + ".height",
                        (int) e.pos.height);
                e.init();
                entidades.add(e);
            }

        } catch (Exception ex) {
            // Se não conseguir ler (der erro), nada faz.
        }
    }
}
