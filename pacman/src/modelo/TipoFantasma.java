package modelo;
// define os tipos de fantasma disponíveis, usado pelo leitorcsv para usar o subtipo correto
public enum TipoFantasma {
    blinky,  //  chance perseguir 100%
    pinky,   // persegue 75%
    inky,    // persegue 50%
    clyde    // persegue 25%
}
